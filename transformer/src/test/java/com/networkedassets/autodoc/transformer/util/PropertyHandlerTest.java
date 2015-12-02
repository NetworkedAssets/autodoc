package com.networkedassets.autodoc.transformer.util;

import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class PropertyHandlerTest {
    @Test
    public void testGetValue() throws IOException {
        PropertyHandler propertyHandler = PropertyHandler.getInstance();
        assertNotNull(propertyHandler);

        assertNotNull(propertyHandler.getValue("jetty.port", "8050"));
        assertEquals(propertyHandler.getValue("jetty.port", "def"), "8050");
        assertEquals(propertyHandler.getValue("not-found-key", "default-value"), "default-value");

        assertEquals(propertyHandler.getValue("settings.filename", "default-value"), "transformerSettings.ser");
        assertEquals(propertyHandler.getValue("doclet.filename", "default-value"), "json-doclet-1.0.7.jar");
        assertEquals(propertyHandler.getValue("doclet.classname", "default-value"), "com.github.markusbernhardt.xmldoclet.XmlDoclet");
        assertEquals(propertyHandler.getValue("doclet.outfilename", "default-value"), "javadoc.json");

    }
}
