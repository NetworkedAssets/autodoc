package com.networkedassets.autodoc.transformer.util.uml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.JSONException;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.WriterConfig;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.handleRepoPush.DocumentationPiece;
import com.networkedassets.autodoc.transformer.handleRepoPush.core.DocumentationType;
import com.networkedassets.autodoc.transformer.util.javadoc.JavadocConverter;

public class UmlClassDiagramConverter extends JavadocConverter {

	private static final String newline = System.getProperty("line.separator");
	private final Path plantUmlPath;

	public UmlClassDiagramConverter(Path jsonJavaDocPath, Path plantUmlPath) {
		super(jsonJavaDocPath);
		this.plantUmlPath = plantUmlPath;
	}

	public Documentation convert() throws JSONException, IOException {
		JsonObject javadocObj = javadocToMainAsJson();
		JsonObject umlObj = plantUmlDependencyToJson();
		JsonArray output = Json.array().asArray();
		List<String> target = Lists.newArrayList();
		javadocObj.get("entities").asObject().names().stream()
				.map(n -> javadocObj.get("entities").asObject().get(n).asObject().names())
				.forEach(p -> target.addAll(p));

		umlObj.get("relations").asArray().forEach(item -> {
			if (target.contains(item.asObject().getString("target", ""))) {
				output.add(item.asObject());
			}
		});

		javadocObj.add("relations", output);

		Documentation documentation = new Documentation(ImmutableList.of(
				new DocumentationPiece("MAIN_PIECE", "MAIN_PIECE", javadocObj.toString(WriterConfig.PRETTY_PRINT))));
		documentation.setType(DocumentationType.UML_CLASS_DIAGRAM);
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

}
