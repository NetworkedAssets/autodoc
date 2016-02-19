package com.networkedassets.autodoc.transformer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyHandler {
	private static PropertyHandler instance = null;
	private static Logger log = LoggerFactory.getLogger(PropertyHandler.class);

	private Properties defaultProperties;
	private Properties properties;

	private PropertyHandler() {
		initProperties();
		initDefaultProperties();
	}

	public static synchronized PropertyHandler getInstance() {
		if (instance == null)
			instance = new PropertyHandler();
		return instance;
	}

	private void initProperties() {
		try(InputStream input = new FileInputStream("./transformer.properties")) {
			properties = new Properties();
			properties.load(input);
		} catch (IOException e) {
			properties = null;
			log.info("transformer.properties file was not found, transformer_defaults.properties will be used instead");
		}
	}

	private void initDefaultProperties() {
		try(InputStream input = getClass().getClassLoader().getResourceAsStream("transformer_defaults.properties")) {
			defaultProperties = new Properties();
			defaultProperties.load(input);
		} catch (IOException e) {
			log.error("Could not find transformer_defaults.properties file" + e);
		}
	}

	/**
	 * Returns property held under specified <code>key</code>.
	 *
	 * @param key
	 * @return Property value from transformer properties or default value from transformer_defaults properties file
	 */
	public String getValue(String key) {
		Object property = (properties != null && properties.containsKey(key)) ? properties.get(key) : defaultProperties.get(key);
		String propertyString = String.valueOf(property);
		log.info("Property obtained, value: " + propertyString);
		return String.valueOf(property);
	}
}