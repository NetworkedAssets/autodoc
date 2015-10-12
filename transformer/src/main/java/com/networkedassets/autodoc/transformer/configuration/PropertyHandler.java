package com.networkedassets.autodoc.transformer.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyHandler {

	private static PropertyHandler instance = null;

	private Properties props = null;

	private PropertyHandler() throws IOException {

		InputStream input = new FileInputStream("config.properties");
		this.props = new Properties();
		this.props.load(input);
		input.close();
	}

	public static synchronized PropertyHandler getInstance() throws IOException {
		if (instance == null)
			instance = new PropertyHandler();
		return instance;
	}

	/**
	 * Returns property held under specified <code>key</code>.
	 * 
	 * @param key
	 * @return value for specified <code>key</code> or null if not defined.
	 */
	public String getValue(String key) {
		Object value = props.get(key);

		return (value != null) ? String.valueOf(value) : null;
	}
}
