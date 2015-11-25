package com.networkedassets.autodoc.transformer.util.javadoc;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

import org.json.JSONException;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.WriterConfig;
import com.google.common.collect.ImmutableList;
import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.handleRepoPush.DocumentationPiece;
import com.networkedassets.autodoc.transformer.handleRepoPush.core.DocumentationType;

public class JavadocConverter {

	public final static int PRETTY_PRINT_INDENT_FACTOR = 4;
	private final Path jsonJavaDocPath;

	public JavadocConverter(Path jsonJavaDocPath) {
		this.jsonJavaDocPath = jsonJavaDocPath;

	}

	public Documentation convert() throws JSONException, IOException {

		Documentation documentation = new Documentation(ImmutableList.of(
				new DocumentationPiece("MAIN_PIECE", "MAIN_PIECE",
						javadocToMainAsJson().toString(WriterConfig.PRETTY_PRINT)),
				new DocumentationPiece("INDEX", "INDEX", javadocToIndexAsJson().toString(WriterConfig.PRETTY_PRINT))));
		documentation.setType(DocumentationType.JAVADOC);
		return documentation;

	}

	protected JsonObject javadocToMainAsJson() throws IOException {

		JsonObject rootObj = Json.parse(new FileReader(this.jsonJavaDocPath.toString())).asObject();
		JsonValue packages = rootObj.get("root").asObject().get("package").asArray();

		JsonObject entities_ = Json.object();
		for (JsonValue item : packages.asArray()) {
			JsonObject package_ = Json.object().add("qualified", item.asObject().getString("name", "")).add("type",
					"package");
			item.asObject().names().stream().filter(name -> item.asObject().get(name).isArray())
					.forEach(s -> item.asObject().get(s).asArray().forEach(e -> {
						JsonArray arr = Json.array().asArray();
						e.asObject().names().stream().filter(modyfiers -> e.asObject().get(modyfiers).isTrue())
								.forEach(m -> arr.add(m));
						e.asObject().add("modifiers", (JsonValue) arr).add("type", s);
						package_.add(e.asObject().getString("qualified", ""), (JsonValue) e.asObject());
					}));
			entities_.add(package_.getString("qualified", ""), (JsonValue) package_);
		}
		rootObj.remove("root");
		rootObj.add("entities", (JsonValue) entities_);
		return rootObj;
	}

	private JsonObject javadocToIndexAsJson() throws JSONException, IOException {

		JsonObject rootObj = Json.parse(new FileReader(this.jsonJavaDocPath.toString())).asObject();
		JsonValue packages = rootObj.get("root").asObject().get("package").asArray();

		JsonObject entities = Json.object();
		JsonArray arrayOfPackages = Json.array().asArray();
		for (JsonValue item : packages.asArray()) {
			JsonObject package_ = Json.object();
			package_.add("name", item.asObject().getString("name", ""));
			arrayOfPackages.add((JsonValue) package_);
			JsonArray arrayOfClasses = Json.array().asArray();

			item.asObject().names().stream().filter(name -> item.asObject().get(name).isArray())
					.forEach(s -> item.asObject().get(s).asArray().forEach(e -> {
						JsonObject classes = Json.object().add("name", e.asObject().getString("name", ""))
								.add("qualified", e.asObject().getString("qualified", "")).add("type", s);
						arrayOfClasses.add(classes);

					}));
			package_.add("children", arrayOfClasses);
		}

		entities.add("package", arrayOfPackages);
		rootObj.remove("root");
		rootObj.add("entities", entities);

		return rootObj;
	}

}
