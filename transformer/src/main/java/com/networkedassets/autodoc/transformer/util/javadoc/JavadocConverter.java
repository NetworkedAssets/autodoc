package com.networkedassets.autodoc.transformer.util.javadoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

		// TODO: Separate the documentation into pieces
		Documentation documentation = new Documentation(ImmutableList.of(
				new DocumentationPiece("MAIN_PIECE", "MAIN_PIECE",
						javadocToMainAsJson().toString(PRETTY_PRINT_INDENT_FACTOR)),
				new DocumentationPiece("INDEX", "INDEX", javadocToIndexAsJson().toString(PRETTY_PRINT_INDENT_FACTOR))));
		documentation.setType(DocumentationType.JAVADOC);
		return documentation;

	}

	private JSONObject javadocToMainAsJson() throws JSONException, IOException {
		JSONObject rootObj = new JSONObject(new String(Files.readAllBytes(this.jsonJavaDocPath), "UTF-8"));
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
		rootObj.put("entities", ent);
		return rootObj;
	}

	private JSONObject javadocToIndexAsJson() throws JSONException, IOException {
		JSONObject rootObj = new JSONObject(new String(Files.readAllBytes(this.jsonJavaDocPath), "UTF-8"));
		JSONObject root = rootObj.getJSONObject("root");
		JSONArray arrayOfPackages = root.getJSONArray("package");

		JSONObject entities_ = new JSONObject();
		JSONArray arrayOfPackages_ = new JSONArray();

		for (int i = 0; i < arrayOfPackages.length(); i++) {

			JSONObject package_ = new JSONObject();

			package_.put("name", arrayOfPackages.getJSONObject(i).getString("name"));
			arrayOfPackages_.put(package_);

			Iterator<?> keys = arrayOfPackages.getJSONObject(i).keys();
			while (keys.hasNext()) {
				String key = keys.next().toString();
				JSONArray entities = arrayOfPackages.getJSONObject(i).optJSONArray(key);
				if (entities != null) {
					for (int j = 0; j < entities.length(); j++) {
						if (entities.optJSONObject(j) != null) {

							if (entities.optJSONObject(j).optString("qualified") != null) {
								JSONObject class_ = new JSONObject();
								JSONArray arrayOfClasses = new JSONArray();
								class_.put("name", entities.getJSONObject(j).getString("name"));
								class_.put("qualified", entities.getJSONObject(j).getString("qualified"));
								arrayOfClasses.put(class_);
								package_.put("children", arrayOfClasses);

							}

						}
					}
				}
				;
			}

		}

		entities_.put("package", arrayOfPackages_);
		rootObj.remove("root");
		rootObj.put("entities", entities_);
		return rootObj;
	}

}
