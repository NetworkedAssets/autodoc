package com.networkedassets.autodoc.transformer.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class PropertyHandlerTest {
	private PropertyHandler propertyHandler;

	@Before
	public void createPropertyHandler() throws IOException {
		propertyHandler = PropertyHandler.getInstance();
	}

	//TODO: fix: fit the tests to new PropertyHandler
	/*@Test
	public void testGetValuePort() {
		assertNotNull(propertyHandler);

		assertNotNull(propertyHandler.getValue("jetty.port", "8050"));
		assertEquals(propertyHandler.getValue("jetty.port", "def"), "8050");
	}

	@Test
	public void testGetValueNotFoundKey() {
		assertNotNull(propertyHandler);

		assertNotNull(propertyHandler.getValue("not-found-key", "default-value"));
		assertEquals(propertyHandler.getValue("not-found-key", "default-value"), "default-value");
	}

	@Test
	public void testGetValueSeetingsFilename() {
		assertNotNull(propertyHandler);

		assertNotNull(propertyHandler.getValue("settings.filename", "transformerSettings.ser"));
		assertEquals(propertyHandler.getValue("settings.filename", "default-value"), "transformerSettings.ser");
	}*/

}
