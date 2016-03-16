package com.networkedassets.autodoc.transformer.util;

import com.networkedassets.autodoc.transformer.server.Transformer;
import com.networkedassets.autodoc.transformer.settings.TransformerSettings;
import org.hibernate.validator.internal.constraintvalidators.URLValidator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PropertyHandlerTest {
	private static Logger log = LoggerFactory.getLogger(PropertyHandlerTest.class);

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
		assertEquals(propertyHandler.getValue("settings.filename"), "transformerSettings.settings");
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

	@Test
	public void testCreatingTransformerSettingsObjectWithPathAfterPortWorksProperly() {
		String host = "http://wp.pl/";
		int port = 1231;
		String path = "/path/to/something/";

		TransformerSettings transformerSettings = new TransformerSettings();
		transformerSettings.setAddress(host, port, path);

		log.info("Testing URI: " + transformerSettings.getAddress());
	}

	//TODO: jetty. prefix needs to be deleted when branch is merged to move-to-javaee branch
	@Test
	public void testCreatingTransformerSettingsObjectWithDataFromDefaultPropertiesFile() {
		TransformerSettings transformerSettings = new TransformerSettings();
		transformerSettings.setAddress(
				propertyHandler.getValue("jetty.address"),
				Integer.parseInt(propertyHandler.getValue("jetty.port")),
				propertyHandler.getValue("jetty.path")
		);
		log.info("Testing URI from default_properties.properties: " + transformerSettings.getAddress());
	}

	@Test
	public void testCreatingTransformerSettingsObjectWithNullPathDoesNotThrowNullPointerException() {
		TransformerSettings transformerSettings = new TransformerSettings();
		transformerSettings.setAddress("http://wp.pl/", 1233, null);
	}

}
