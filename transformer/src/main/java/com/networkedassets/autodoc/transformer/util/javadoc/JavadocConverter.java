package com.networkedassets.autodoc.transformer.util.javadoc;

import com.google.common.collect.ImmutableList;
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

public class JavadocConverter {

	public final static int PRETTY_PRINT_INDENT_FACTOR = 4;
	private final Path xmlJavaDocPath;

	public JavadocConverter(Path xmlJavaDocPath) {
		this.xmlJavaDocPath = xmlJavaDocPath;

	}

	public Documentation convert() throws JSONException, IOException {
		JSONObject javadocObj = javadocToJson();

		// TODO: Separate the documentation into pieces
		Documentation documentation = new Documentation(
				ImmutableList.of(
						new DocumentationPiece(
								"MAIN_PIECE",
								"MAIN_PIECE",
								javadocObj.toString(PRETTY_PRINT_INDENT_FACTOR)
						)
				)
		);
		documentation.setType(DocumentationType.JAVADOC);
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

}
