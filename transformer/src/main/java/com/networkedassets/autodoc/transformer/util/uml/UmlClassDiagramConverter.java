package com.networkedassets.autodoc.transformer.util.uml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.handleRepoPush.DocumentationPiece;
import com.networkedassets.autodoc.transformer.handleRepoPush.core.DocumentationType;

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

		List<String> target = Lists.newArrayList();
		List<String> lisfOfEntities = Lists.newArrayList(JSONObject.getNames(javadocObj.getJSONObject("entities")));
		lisfOfEntities.stream()
				.map(p -> Lists
						.newArrayList(JSONObject.getNames(javadocObj.getJSONObject("entities").getJSONObject(p))))
				.forEach(p -> target.addAll(p));
		JSONArray array = umlObj.getJSONArray("relations");

		for (int i = 0; i < array.length(); i++) {
			if (target.contains(array.getJSONObject(i).getString("target"))) {
				output.put(array.getJSONObject(i));
			}
		}

		javadocObj.put("relations", output);

		Documentation documentation = new Documentation(ImmutableList.of(
				new DocumentationPiece("MAIN_PIECE", "MAIN_PIECE", javadocObj.toString(PRETTY_PRINT_INDENT_FACTOR))));
		documentation.setType(DocumentationType.UML_CLASS_DIAGRAM);
		return documentation;

	}

	private JSONObject javadocToJson() throws JSONException, IOException {
		JSONObject rootObj = new JSONObject(new String(Files.readAllBytes(this.xmlJavaDocPath), "UTF-8"));
		JSONObject root = rootObj.getJSONObject("root");
		JSONArray arrayOfPackages = root.getJSONArray("package");

		JSONObject ent = new JSONObject();

		for (int i = 0; i < arrayOfPackages.length(); i++) {
			JSONObject obj = new JSONObject();
			obj.put("qualified", arrayOfPackages.getJSONObject(i).getString("name"));
			obj.put("type", "package");
			Iterator<?> keys = arrayOfPackages.getJSONObject(i).keys();
			while (keys.hasNext()) {
				String key = keys.next().toString();
				JSONArray entities = arrayOfPackages.getJSONObject(i).optJSONArray(key);
				if (entities != null) {
					for (int j = 0; j < entities.length(); j++) {
						if (entities.optJSONObject(j) != null) {

							if (entities.optJSONObject(j).optString("qualified") != null) {
								entities.optJSONObject(j).put("type", key);
								obj.put(entities.getJSONObject(j).getString("qualified"), entities.getJSONObject(j));
							}

						}
					}
				}
				;
			}
			ent.put(arrayOfPackages.getJSONObject(i).getString("name"), obj);

		}

		rootObj.remove("root");
		rootObj.put("Entities", ent);
		return rootObj;
	}

	private JSONObject plantUmlDependencyToJson() throws IOException {

		String plantUmlDependency = new String(Files.readAllBytes(this.plantUmlPath), "UTF-8");

		return new JSONObject(
				String.format("{\"relations\":[%s]}", Pattern.compile(newline).splitAsStream(plantUmlDependency)
						.filter(s -> s.contains(">")).map(this::transform).sorted().collect(Collectors.joining(","))));
	}

	private String transform(String s) {

		String[] parts = s.split(" ");
		return String.format("{\"source\":\"%s\",\"type\":\"%s\",\"target\":\"%s\"}", parts[0],
				UmlRelationship.fromDescription(parts[1]), parts[2]);

	}

}
