package com.networkedassets.autodoc.transformer.handleRepoPush.core;

import com.github.markusbernhardt.xmldoclet.xjc.Root;
import com.networkedassets.autodoc.transformer.handleRepoPush.Code;
import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.util.PropertyHandler;
import com.networkedassets.autodoc.transformer.util.javadoc.Javadoc;
import com.networkedassets.autodoc.transformer.util.javadoc.JavadocConverter;
import com.networkedassets.autodoc.transformer.util.javadoc.JavadocException;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;

/**
 * Created by mrobakowski on 11/13/2015.
 */
public class JavadocGenerator implements DocumentationGenerator {

	private static Logger log = LoggerFactory.getLogger(JavadocGenerator.class);

	@Override
	public Documentation generateFrom(Code code) {
		try {
			String docletPath = new File(getDocletFilenameFromProperties()).getAbsolutePath();
			String docletClassname = getDocletClassnameFromProperties();
			System.out.println("doclet path: " + docletPath + "\ndoclet class: " + docletClassname);
			Root docRoot = Javadoc.structureFromDirectory(code.getCodePath());

			return JavadocConverter.convert(docRoot);

		} catch (JavadocException | JSONException | JAXBException e) {
			throw new RuntimeException("Couldn't generate Javadoc", e);
		}
	}

	protected String getDocletFilenameFromProperties() {

		final String defaultDocletFileName = "json-doclet-1.0.7.jar";
		String docletFileName = "";

		try {
			docletFileName = PropertyHandler.getInstance().getValue("doclet.filename", defaultDocletFileName);
		} catch (IOException e) {
			log.error("Couldn't load the configuration file", e);
			return defaultDocletFileName;
		}
		return docletFileName;
	}

	protected String getDocletClassnameFromProperties() {

		final String defaultDocletClassName = "com.github.markusbernhardt.xmldoclet.XmlDoclet";
		String docletClassName = "";

		try {
			docletClassName = PropertyHandler.getInstance().getValue("doclet.classname", defaultDocletClassName);
		} catch (IOException e) {
			log.error("Couldn't load the configuration file", e);
			return defaultDocletClassName;
		}
		return docletClassName;
	}

	protected String getDocletOutFilenameFromProperties() {

		final String defaultDocletClassName = "javadoc.json";
		String docletClassName = "";

		try {
			docletClassName = PropertyHandler.getInstance().getValue("doclet.oufilename", defaultDocletClassName);
		} catch (IOException e) {
			log.error("Couldn't load the configuration file", e);
			return defaultDocletClassName;
		}
		return docletClassName;
	}
}
