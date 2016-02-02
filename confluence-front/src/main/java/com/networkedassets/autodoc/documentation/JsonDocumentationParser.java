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
                .map(s -> findEntityByFQCN(s, entitiesNode).get())
                .collect(Collectors.toSet());
    }

    private Set<String> getEntitiesNamesFromRelations(String docPieceName, Set<Relation> foundRelationsSet) {
        Set<String> resultSet = new HashSet<>();
        for (Relation relation : foundRelationsSet) {
            if(relation.getSource().equals(docPieceName)){
                resultSet.add(relation.getTarget());
            }else{
                resultSet.add(relation.getSource());
            }
        }
        resultSet.add(docPieceName);
        return resultSet;
    }

    //TODO: not too pretty, I know.. still in progress
    public Optional<String> composeJSON(String docPieceName) throws IOException {
        Set<Relation> relationsSet = findRelations(docPieceName);
        Set<Entity> entitiesSet = findEntities(getEntitiesNamesFromRelations(docPieceName, relationsSet));

        JSONObject jsonObject = new JSONObject();

        Set<String> allPackages = findAllPackages(entitiesSet);
        JSONObject packagesJson = new JSONObject();
        for (String packageName : allPackages) {
            // building object in the package START
            JSONObject packageValue = new JSONObject()
                    .put("qualified", packageName)
                    .put("type", "package");
            for (Entity entity : entitiesSet) {
                if (entity.getPackageName().equals(packageName)){
                    packageValue.put(entity.getFqcn(), new JSONObject(entity.getJSONdata()));
                }
            }
            // building object in the package END
            packagesJson.put(packageName, packageValue);
        }
        jsonObject.put("entities", packagesJson);
        jsonObject.put("relations", new JSONArray(relationsSet));

        return Optional.of(jsonObject.toString());
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
    * Returns JSON string describing single fqcn class
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
