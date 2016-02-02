package com.networkedassets.autodoc.documentation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networkedassets.autodoc.documentation.data.Entity;
import com.networkedassets.autodoc.documentation.data.Relation;
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
//TODO: test, test, test! still needs to be corrected and beautified
public class JsonDocumentationParser {
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode rootNode;

    public JsonDocumentationParser(String JSON) throws IOException {
        this.rootNode = mapper.readTree(JSON);
    }

    private Set<Relation> findRelations(String docPieceName) throws IOException {
        JsonNode relationsNode = rootNode.get("relations");
        Set<Relation> allRelationsSet = mapper.readValue(relationsNode.toString(), new TypeReference<Set<Relation>>(){});
        return allRelationsSet.stream()
                .filter(n -> n.getSource().equals(docPieceName) || n.getTarget().equals(docPieceName))
                .collect(Collectors.toSet());
    }

    private Set<Entity> findEntities(Set<String> entitiesDocPieceNames) {
        JsonNode entitiesNode = rootNode.get("entities");
        return entitiesDocPieceNames.stream()
                .map(s -> findEntityByFQCN(s, entitiesNode))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    private Set<String> getEntitiesNamesFromRelations(String docPieceName, Set<Relation> foundRelationsSet) {
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

    //TODO: not too pretty, I know.. still in progress
    public Optional<String> composeJSON(String docPieceName) throws IOException {
        Set<Relation> relationsSet = findRelations(docPieceName);
        Set<Entity> entitiesSet = findEntities(getEntitiesNamesFromRelations(docPieceName, relationsSet));

        if(! (entitiesSet.isEmpty() || relationsSet.isEmpty()) ) { // prawo DeMorgana sie przydalo.. wow
            Set<String> allPackages = findAllPackages(entitiesSet);
            JSONObject responseJSONObject = new JSONObject();

            responseJSONObject.put("entities", buildEntitiesPackageValue(allPackages, entitiesSet));
            responseJSONObject.put("relations", new JSONArray(relationsSet));
            return Optional.of(responseJSONObject.toString());
        }else{
            return Optional.empty();
        }
    }

    private JSONObject buildEntitiesPackageValue(Set<String> allPackages, Set<Entity> entitiesSet) {
        JSONObject packagesJson = new JSONObject();
        allPackages.stream().forEach(packageName -> {
            //building package defaults: qualified and type
            JSONObject packageValue = new JSONObject()
                    .put("qualified", packageName)
                    .put("type", "package");
            // building objects in the package START
            entitiesSet.stream().filter(entity -> entity.getPackageName().equals(packageName)).forEach(entity -> {
                packageValue.put(entity.getFqcn(), new JSONObject(entity.getJSONdata()));
            });
            // building objects in the package END
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
            //System.out.printf("Removed %s ## \n", docPieceName.substring(packageName.length(), docPieceName.length()));
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
    * Returns Entity object describing single FQCN class
    * */
    private Optional<Entity> findEntityByFQCN(String fqcn, JsonNode packagesNode) {
        Optional<String> packageName = seekForPackage(fqcn, copyIteratorToSet(packagesNode.fieldNames()));
        if(packageName.isPresent()) {
            JsonNode foundPackageNode = packagesNode.get(packageName.get());
            Set<String> fqcnSet = copyIteratorToSet(foundPackageNode.fieldNames());

            if(fqcnSet.contains(fqcn)) {
                JsonNode foundClassNode = foundPackageNode.get(fqcn);
                return Optional.of(new Entity(packageName.get(), fqcn, foundClassNode.toString()));
            }
        }
        return Optional.empty();
    }
}
