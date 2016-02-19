package com.networkedassets.autodoc.transformer.util;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PropertyHandlerTest {
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	private PropertyHandler propertyHandler;

	@Before
	public void getInstance() {
		this.propertyHandler = PropertyHandler.getInstance();
	}

	@Test
	public void testGetDefaultPortAddressFilename() {
		assertNotNull(propertyHandler.getValue("jetty.port"));
		assertEquals(propertyHandler.getValue("jetty.port"), "8050");

		assertEquals(propertyHandler.getValue("jetty.address"), "http://localhost/");
		assertEquals(propertyHandler.getValue("settings.filename"), "transformerSettings.ser");
	}

	// you need to delete transformer.properties and Transformer.log files after this test manually
	@Test
	@Ignore
	public void testGetCustomPort() throws IOException {
		final String transformerProperties = "./transformer.properties";

		File tempFile = temporaryFolder.newFile(transformerProperties);
		Properties properties = prepareProperties(tempFile);
		properties.store(new FileWriter(transformerProperties), "Its a file for testing, delete it if it won't itself");

		assertEquals(propertyHandler.getValue("jetty.port"), "1234");
	}

	private Properties prepareProperties(File tempFile) throws IOException {
		Properties properties = new Properties();
		properties.load(new FileReader(tempFile));
		properties.put("jetty.port", "1234");
		return properties;
	}

	@Test
	public void testGetValueNotFoundKey() {
		assertEquals(propertyHandler.getValue("not-found-key"), "null");
	}

}
