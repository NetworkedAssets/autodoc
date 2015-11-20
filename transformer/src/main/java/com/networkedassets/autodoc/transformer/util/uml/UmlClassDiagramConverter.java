package com.networkedassets.autodoc.transformer.util.uml;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.handleRepoPush.DocumentationPiece;
import com.networkedassets.autodoc.transformer.handleRepoPush.core.DocumentationType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UmlClassDiagramConverter {

	public final static int PRETTY_PRINT_INDENT_FACTOR = 4;
	private static final String newline = System.getProperty("line.separator");
	private final Path xmlJavaDocPath;
	private final Path plantUmlPath;

	public UmlClassDiagramConverter(Path xmlJavaDocPath, Path plantUmlPath) {
		this.xmlJavaDocPath = xmlJavaDocPath;
		this.plantUmlPath = plantUmlPath;
	}

	public Documentation convert() throws JSONException, IOException {
		JSONObject javadocObj = javadocToJson();
		JSONObject umlObj = plantUmlDependencyToJson();
		JSONArray output = new JSONArray();

		List<String> lisfOfEntities = Lists.newArrayList(JSONObject.getNames(javadocObj.getJSONObject("Entities")));

		JSONArray array = umlObj.getJSONArray("relations");

		for (int i = 0; i < array.length(); i++) {
			if (lisfOfEntities.contains(array.getJSONObject(i).getString("target"))) {
				output.put(array.getJSONObject(i));
			}
		}

		javadocObj.put("relations", output);

		Documentation documentation = new Documentation(
				ImmutableList.of(
						new DocumentationPiece(
								"MAIN_PIECE",
								"MAIN_PIECE",
								javadocObj.toString(PRETTY_PRINT_INDENT_FACTOR)
						)
				)
		);
		documentation.setType(DocumentationType.UML_CLASS_DIAGRAM);
		return documentation;

	}

	private JSONObject javadocToJson() throws JSONException, IOException {
		JSONObject xmlJSONObj = new JSONObject();

		xmlJSONObj = XML.toJSONObject(new String(Files.readAllBytes(this.xmlJavaDocPath), "UTF-8"));

		String jsonPrettyString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
		JSONObject rootObj = new JSONObject(jsonPrettyString);
		JSONObject jelObj = rootObj.getJSONObject("jel");
		JSONArray arrayOfClasses = jelObj.getJSONArray("jelclass");

		for (int i = 0; i < arrayOfClasses.length(); i++) {
			jelObj.put(arrayOfClasses.getJSONObject(i).getString("fulltype"), arrayOfClasses.getJSONObject(i));
		}

		jelObj.remove("jelclass");
		rootObj.put("Entities", rootObj.getJSONObject("jel"));
		rootObj.remove("jel");
		return rootObj;
	}

	private JSONObject plantUmlDependencyToJson() throws IOException {

		String plantUmlDependency = new String(Files.readAllBytes(this.plantUmlPath), "UTF-8");

		return new JSONObject(String.format("{\"relations\":[%s]}",
				Pattern.compile(newline).splitAsStream(plantUmlDependency).filter(s -> s.contains(">"))
						.map(this::transform).sorted().collect(Collectors.joining(","))));
	}

	private String transform(String s) {

		String[] parts = s.split(" ");
		return String.format("{\"source\":\"%s\",\"type\":\"%s\",\"target\":\"%s\"}", parts[0],
				UmlRelationship.fromDescription(parts[1]), parts[2]);

	}

}
