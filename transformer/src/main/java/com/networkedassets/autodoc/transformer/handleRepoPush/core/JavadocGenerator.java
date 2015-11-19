package com.networkedassets.autodoc.transformer.handleRepoPush.core;

import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.networkedassets.autodoc.transformer.handleRepoPush.Code;
import com.networkedassets.autodoc.transformer.util.PropertyHandler;
import com.networkedassets.autodoc.transformer.util.javadoc.Javadoc;
import com.networkedassets.autodoc.transformer.util.javadoc.JavadocConverter;
import com.networkedassets.autodoc.transformer.util.javadoc.JavadocException;

/**
 * Created by mrobakowski on 11/13/2015.
 */
public class JavadocGenerator implements DocumentationGenerator {

	private static Logger log = LoggerFactory.getLogger(JavadocGenerator.class);

	@Override
	public Documentation generateFrom(Code code) {
		try {
			Path xmlJavaDocPath = Javadoc.fromDirectory(code.getCodePath(),
					new File(getDocletFilenameFromProperties()).getAbsolutePath(), getDocletClassnameFromProperties());

			return new JavadocConverter(xmlJavaDocPath).convert();

		} catch (JavadocException | JSONException | IOException e) {
			throw new RuntimeException("Couldn't generate Javadoc", e);
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

	private String getDocletClassnameFromProperties() {

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
