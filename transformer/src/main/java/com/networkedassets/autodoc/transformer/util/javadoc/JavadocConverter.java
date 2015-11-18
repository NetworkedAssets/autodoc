package com.networkedassets.autodoc.transformer.util.javadoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;

public class JavadocConverter {

	public final static int PRETTY_PRINT_INDENT_FACTOR = 4;
	private final Path xmlJavaDocPath;

	public JavadocConverter(Path xmlJavaDocPath) {
		this.xmlJavaDocPath = xmlJavaDocPath;

	}

	public Documentation convert() throws JSONException, IOException {
		JSONObject javadocObj = javadocToJson();

		return new Documentation(javadocObj.toString(PRETTY_PRINT_INDENT_FACTOR));

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
