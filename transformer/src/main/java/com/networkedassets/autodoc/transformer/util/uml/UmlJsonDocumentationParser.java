package com.networkedassets.autodoc.transformer.util.uml;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networkedassets.autodoc.transformer.util.uml.data.Entity;
import com.networkedassets.autodoc.transformer.util.uml.data.Relation;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by mtulaza on 2016-02-02.
 */
public class UmlJsonDocumentationParser {
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode rootNode;

    public UmlJsonDocumentationParser(String JSON) throws IOException {
        this.rootNode = mapper.readTree(JSON);
    }

    private Set<Relation> findAllRelations() throws IOException {
        JsonNode relationsNode = rootNode.get("relations");
        return mapper.readValue(relationsNode.toString(), new TypeReference<Set<Relation>>(){});
    }

    private Set<Relation> findRelationsBy(String docPieceName) throws IOException {
        Set<Relation> allRelationsSet = findAllRelations();
        return allRelationsSet.stream()
                .filter(n -> n.getSource().equals(docPieceName) || n.getTarget().equals(docPieceName))
                .collect(Collectors.toSet());
    }

    private Set<Relation> findRelationsBetweenEntities(Set<String> entitiesNames, Set<Relation> allRelations) {
        return allRelations.parallelStream()
                .filter(relation -> entitiesNames.contains(relation.getSource()) && entitiesNames.contains(relation.getTarget()))
                .collect(Collectors.toSet());
    }

    private Set<String> getAllEntitiesNamesFromRelations(String docPieceName, Set<Relation> foundRelationsSet) {
        Set<String> resultSet = new HashSet<>();
        foundRelationsSet.stream().forEach(relation -> {
            if(relation.getSource().equals(docPieceName)){
                resultSet.add(relation.getTarget());
            }else{
                resultSet.add(relation.getSource());
            }
        });
        resultSet.add(docPieceName);
        return resultSet;
    }

    private Set<Entity> findEntities(Set<String> entitiesDocPieceNames) {
        JsonNode entitiesNode = rootNode.get("entities");
        return entitiesDocPieceNames.stream()
                .map(s -> findEntityByDocPieceName(s, entitiesNode))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    public Optional<String> filterAndComposeJSON(String docPieceName) throws IOException {
        Set<String> entitiesNames = getAllEntitiesNamesFromRelations(docPieceName, findRelationsBy(docPieceName));

        Set<Relation> relationsSet = findRelationsBetweenEntities(entitiesNames, findAllRelations());
        Set<Entity> entitiesSet = findEntities(getAllEntitiesNamesFromRelations(docPieceName, relationsSet));

        if(! (entitiesSet.isEmpty() || relationsSet.isEmpty()) ) { // prawo deMorgana sie przydalo.. wow
            Set<String> allPackages = findAllPackages(entitiesSet);
            JSONObject responseJSONObject = new JSONObject();

            responseJSONObject.put("entities", buildPackagesAndTheirValues(allPackages, entitiesSet));
            responseJSONObject.put("relations", new JSONArray(relationsSet));
            return Optional.of(responseJSONObject.toString());
        }else{
            return Optional.empty();
        }
    }

    private JSONObject buildPackagesAndTheirValues(Set<String> allPackages, Set<Entity> entitiesSet) {
        JSONObject packagesJson = new JSONObject();
        allPackages.stream().forEach(packageName -> {
            //building package defaults: qualified and type
            JSONObject packageValue = new JSONObject()
                    .put("qualified", packageName)
                    .put("type", "package");
            // building objects in the package START
            entitiesSet.stream().filter(entity -> entity.getPackageName().equals(packageName)).forEach(entity -> {
                packageValue.put(entity.getDocPieceName(), new JSONObject(entity.getJSONdata()));
            });

            packagesJson.put(packageName, packageValue);
        });
        return packagesJson;
    }

    private Set<String> findAllPackages(Set<Entity> entitySet) {
        return entitySet.stream().map(Entity::getPackageName).distinct().collect(Collectors.toSet());
    }

    private Optional<String> seekForPackage(String docPieceName, Set<String> packagesSet) {
        if(docPieceName.contains(".")){
            String packageName = docPieceName.substring(0, docPieceName.lastIndexOf("."));
            if(packagesSet.contains(packageName)) {
                return Optional.of(packageName);
            }else{
                return seekForPackage(packageName, packagesSet);
            }
        }else {
            return Optional.empty();
        }
    }

    private <T> Set<T> copyIteratorToSet(Iterator<T> iterator) {
        Set<T> result = new HashSet<>();
        iterator.forEachRemaining(result::add);
        return result;
    }

    /*
    * Returns Entity object describing single docPieceName class value in JSON
    * */
    private Optional<Entity> findEntityByDocPieceName(String docPieceName, JsonNode packagesNode) {
        Optional<String> packageName = seekForPackage(docPieceName, copyIteratorToSet(packagesNode.fieldNames()));
        if(packageName.isPresent()) {
            JsonNode foundPackageNode = packagesNode.get(packageName.get());
            Set<String> fqcnSet = copyIteratorToSet(foundPackageNode.fieldNames());

            if(fqcnSet.contains(docPieceName)) {
                JsonNode foundClassNode = foundPackageNode.get(docPieceName);
                return Optional.of(new Entity(packageName.get(), docPieceName, foundClassNode.toString()));
            }
        }
        return Optional.empty();
    }
}
