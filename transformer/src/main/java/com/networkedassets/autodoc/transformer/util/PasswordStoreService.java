package com.networkedassets.autodoc.transformer.util;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/*
    Class responsible for building and storing random password in
    the file next to the transformer.jar file
 */
public class PasswordStoreService {
    private static Logger log = LoggerFactory.getLogger(PasswordStoreService.class);

    private File file;
    private Properties properties;

    public PasswordStoreService(String filename) {
        this.file = new File(filename);
        this.properties = new Properties();

        try {
            this.file.createNewFile();
            this.properties.load(new FileInputStream(this.file));
        } catch (IOException e) {
            log.error("Cannot load or create properties file.. " + e);
        }
    }

    /**
     * @param content Property type to get
     * @return Property from properties file or creates new if not exist
     */
    public String getProperty(PropertyType content) {
        String contentName = content.name().toLowerCase();
        if(properties.containsKey(contentName)) {
            log.info(contentName + " property from properties file will be returned..");
            return properties.getProperty(contentName);
        }
        log.info("No " + contentName + " property in the file; Generating and storing..");
        String generatedProperty = RandomStringUtils.randomAlphanumeric(content.getLength());
        properties.put(contentName, generatedProperty);
        try {
            properties.store(new FileOutputStream(this.file), "");
        } catch (IOException e) {
            log.error("Properties file was not found.. " + e);
            return null;
        }
        return generatedProperty;
    }

    public enum PropertyType {
        PASSWORD(30), SALT(10);
        private int length;

        PropertyType(int length){
            this.length = length;
        }

        public int getLength() {
            return length;
        }
    }
}
