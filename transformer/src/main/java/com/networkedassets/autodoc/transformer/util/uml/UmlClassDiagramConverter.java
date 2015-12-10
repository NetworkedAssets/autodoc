package com.networkedassets.autodoc.transformer.util.uml;

import com.eclipsesource.json.*;
import com.github.markusbernhardt.xmldoclet.xjc.Root;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.networkedassets.autodoc.transformer.handleRepoPush.Code;
import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.handleRepoPush.DocumentationPiece;
import com.networkedassets.autodoc.transformer.handleRepoPush.core.DocumentationType;
import com.networkedassets.autodoc.transformer.handleRepoPush.core.JavadocGenerator;
import com.networkedassets.autodoc.transformer.util.javadoc.Javadoc;
import com.networkedassets.autodoc.transformer.util.javadoc.JavadocException;
import org.json.JSONException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UmlClassDiagramConverter {

    private static final String newline = System.getProperty("line.separator");
    private final Path plantUmlPath;
    private final Code code;

    public UmlClassDiagramConverter(Path plantUmlPath, Code code) {
        this.plantUmlPath = plantUmlPath;
        this.code = code;
    }

    public Documentation convert() throws JSONException, IOException, JAXBException, JavadocException {
        Root r = JavadocGenerator.getCachedRoot() != null ?
                JavadocGenerator.getCachedRoot() :
                Javadoc.structureFromDirectory(code.getCodePath());
        JsonObject javadocObj = javadocToMainAsJson(r);
        JsonObject umlObj = plantUmlDependencyToJson();
        JsonArray output = Json.array().asArray();
        List<String> target = Lists.newArrayList();
        javadocObj.get("entities").asObject().names().stream()
                .map(n -> javadocObj.get("entities").asObject().get(n).asObject().names())
                .forEach(target::addAll);

        umlObj.get("relations").asArray().forEach(item -> {
            if (target.contains(item.asObject().getString("target", ""))) {
                output.add(item.asObject());
            }
        });

        javadocObj.add("relations", output);

        Documentation documentation = new Documentation(ImmutableList.of(
                new DocumentationPiece("all", "all", javadocObj.toString(WriterConfig.PRETTY_PRINT))));
        documentation.setType(DocumentationType.UML);
        return documentation;
    }

    private JsonObject plantUmlDependencyToJson() throws IOException {

        String plantUmlDependency = new String(Files.readAllBytes(this.plantUmlPath), "UTF-8");

        return Json
                .parse(String.format("{\"relations\":[%s]}", Pattern.compile(newline).splitAsStream(plantUmlDependency)
                        .filter(s -> s.contains(">")).map(this::transform).sorted().collect(Collectors.joining(","))))
                .asObject();
    }

    private String transform(String s) {

        String[] parts = s.split(" ");
        return String.format("{\"source\":\"%s\",\"type\":\"%s\",\"target\":\"%s\"}", parts[0],
                UmlRelationship.fromDescription(parts[1]), parts[2]);

    }

    private JsonObject javadocToMainAsJson(Root javadocRoot) throws IOException, JAXBException {
        String json = JavadocGenerator.jaxbToJson(Root.class, javadocRoot);
        JsonObject rootObj = Json.parse(new StringReader(json)).asObject();
        JsonArray packages = rootObj.get("root").asObject().get("package").asArray();

        JsonObject entities_ = Json.object();
        for (JsonValue item : packages) {
            JsonObject package_ = Json.object().add("qualified", item.asObject().getString("name", "")).add("type",
                    "package");
            item.asObject().names().stream().filter(names -> item.asObject().get(names).isArray())
                    .forEach(entityType ->
                            item.asObject().get(entityType).asArray().forEach(entity ->
                                    package_.add(entity.asObject().getString("qualified", ""), entity.asObject())));
            entities_.add(package_.getString("qualified", ""), package_);
        }
        rootObj.remove("root");
        rootObj.add("entities", entities_);
        return rootObj;
    }

}
