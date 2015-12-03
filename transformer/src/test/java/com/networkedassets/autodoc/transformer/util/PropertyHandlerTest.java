package com.networkedassets.autodoc.transformer.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

public class PropertyHandlerTest {
	@Test
	public void testGetValuePort() throws IOException {
		PropertyHandler propertyHandler = PropertyHandler.getInstance();
		assertNotNull(propertyHandler);

		assertNotNull(propertyHandler.getValue("jetty.port", "8050"));
		assertEquals(propertyHandler.getValue("jetty.port", "def"), "8050");
	}
	@Test
	public void testGetValueNotFoundKey() throws IOException {
		PropertyHandler propertyHandler = PropertyHandler.getInstance();
		assertNotNull(propertyHandler);

		assertNotNull(propertyHandler.getValue("not-found-key", "default-value"));
		assertEquals(propertyHandler.getValue("not-found-key", "default-value"), "default-value");
	}
	@Test
	public void testGetValueSeetingsFilename() throws IOException {
		PropertyHandler propertyHandler = PropertyHandler.getInstance();
		assertNotNull(propertyHandler);

		assertNotNull(propertyHandler.getValue("settings.filename", "transformerSettings.ser"));
		assertEquals(propertyHandler.getValue("settings.filename", "default-value"), "transformerSettings.ser");
	}
	@Test
	public void testGetValueDocletFilename() throws IOException {
		PropertyHandler propertyHandler = PropertyHandler.getInstance();
		assertNotNull(propertyHandler);

		assertNotNull(propertyHandler.getValue("doclet.filename", "json-doclet-1.0.7.jar"));
		assertEquals(propertyHandler.getValue("doclet.filename", "default-value"), "json-doclet-1.0.7.jar");
		}
	@Test
	public void testGetValueDocletClassname() throws IOException {
		PropertyHandler propertyHandler = PropertyHandler.getInstance();
		assertNotNull(propertyHandler);

		assertNotNull(propertyHandler.getValue("doclet.classname", "com.github.markusbernhardt.xmldoclet.XmlDoclet"));
		assertEquals(propertyHandler.getValue("doclet.classname", "default-value"),
				"com.github.markusbernhardt.xmldoclet.XmlDoclet");
	}
	@Test
	public void testGetValueDocletOutFilename() throws IOException {
		PropertyHandler propertyHandler = PropertyHandler.getInstance();
		assertNotNull(propertyHandler);

		assertNotNull(propertyHandler.getValue("doclet.outfilename", "javadoc.json"));
		assertEquals(propertyHandler.getValue("doclet.outfilename", "default-value"), "javadoc.json");
	}
}
