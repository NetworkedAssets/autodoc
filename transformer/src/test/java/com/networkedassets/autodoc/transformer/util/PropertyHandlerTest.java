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
        assertEquals(propertyHandler.getValue("not-found-key", "default-value"), "default-value");

        // TODO: test props
    }
}
