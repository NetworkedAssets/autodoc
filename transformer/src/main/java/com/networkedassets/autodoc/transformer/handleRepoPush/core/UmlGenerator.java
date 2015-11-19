package com.networkedassets.autodoc.transformer.handleRepoPush.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.networkedassets.autodoc.transformer.handleRepoPush.Code;
import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.manageSettings.core.SettingsManager;
import com.networkedassets.autodoc.transformer.util.PropertyHandler;
import com.networkedassets.autodoc.transformer.util.javadoc.Javadoc;
import com.networkedassets.autodoc.transformer.util.javadoc.JavadocException;
import com.networkedassets.autodoc.transformer.util.uml.PlantUML;
import com.networkedassets.autodoc.transformer.util.uml.PlantUMLException;
import com.networkedassets.autodoc.transformer.util.uml.UmlClassDiagramConverter;

/**
 * Created by mrobakowski on 11/13/2015.
 */
public class UmlGenerator implements DocumentationGenerator {

	private static Logger log = LoggerFactory.getLogger(UmlGenerator.class);

	@Override
	public Documentation generateFrom(Code code) {

		try {

			Path plantUmlPath = PlantUML.fromDirectory(code.getCodePath(), null, null);
			Path xmlJavaDocPath = Javadoc.fromDirectory(code.getCodePath(),
					new File(getDocletFilenameFromProperties()).getAbsolutePath(), getDocletClassNameFromProperties());

			return new UmlClassDiagramConverter(xmlJavaDocPath, plantUmlPath).convert();

		} catch (PlantUMLException | JavadocException | JSONException | IOException e)

		{
			throw new RuntimeException("Couldn't generate UML", e);
		}
	}

	private String getDocletFilenameFromProperties() {

		final String defaultDocletFileName = "jeldoclet.jar";
		String docletFileName = "";

		try {
			docletFileName = PropertyHandler.getInstance().getValue("doclet.filename", defaultDocletFileName);
		} catch (IOException e) {
			log.error("Couldn't load the configuration file", e);
			return defaultDocletFileName;
		}
		return docletFileName;
	}

	private String getDocletClassNameFromProperties() {

		final String defaultDocletClassName = "jeldoclet.jar";
		String docletClassName = "";

		try {
			docletClassName = PropertyHandler.getInstance().getValue("doclet.filename", defaultDocletClassName);
		} catch (IOException e) {
			log.error("Couldn't load the configuration file", e);
			return defaultDocletClassName;
		}
		return docletClassName;
	}

}
