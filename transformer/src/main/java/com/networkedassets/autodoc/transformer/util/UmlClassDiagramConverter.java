package com.networkedassets.autodoc.transformer.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;

public class UmlClassDiagramConverter {

	public final static int PRETTY_PRINT_INDENT_FACTOR = 4;
	private static final String newline = System.getProperty("line.separator");
	private final Path xmlJavaDocPath;
	private final Path plantUmlPath;

	public UmlClassDiagramConverter(Path xmlJavaDocPath, Path plantUmlPath) {
		this.xmlJavaDocPath = xmlJavaDocPath;
		this.plantUmlPath = plantUmlPath;
	}

	public Documentation convert() {
		return null;

	}

	private JSONObject javadocToJson() throws JSONException, UnsupportedEncodingException, IOException {
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

	public JSONObject plantUmlDependencyToJson() throws UnsupportedEncodingException, IOException {

		String plantUmlDependency = new String(Files.readAllBytes(this.plantUmlPath), "UTF-8");

		return  new JSONObject(String.format("{\"relations\":[%s]}", Pattern.compile(newline).splitAsStream(plantUmlDependency)
				.filter(s -> s.contains(">")).map(s -> transform(s)).sorted().collect(Collectors.joining(","))));
	}

	private String transform(String s){
		
		String[] parts = s.split(" ");
		return String.format("{\"source\":\"%s\",\"type\":\"%s\",\"target\":\"%s\"}",parts[0],UmlRelationship.fromDescription(parts[1]),parts[2]);
		  
	}

}
