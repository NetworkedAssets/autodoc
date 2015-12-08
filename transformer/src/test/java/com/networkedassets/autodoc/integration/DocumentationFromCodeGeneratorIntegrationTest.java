package com.networkedassets.autodoc.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networkedassets.autodoc.transformer.handleRepoPush.PushEvent;
import com.networkedassets.autodoc.transformer.handleRepoPush.provide.in.PushEventProcessor;
import com.networkedassets.autodoc.transformer.server.Binder;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;

/**
 * Created by mtulaza on 2015-12-07.
 */
public class DocumentationFromCodeGeneratorIntegrationTest {
    @Inject
    private PushEventProcessor documentationFromCodeGenerator;

    private final String JSON_REQUEST = "{\n" +
            "\"sourceUrl\" : \"http://46.101.240.138:7990/\",\n" +
            "\"projectKey\" : \"APD\",\n" +
            "\"repositorySlug\" : \"javadoc-plugin\",\n" +
            "\"branchId\" : \"master\"\n" +
            "}";

    /**
     * method that injects this test class into ServiceLocator pool of services
     */
    @Before
    public void injectBefore() {
        ServiceLocator serviceLocator = ServiceLocatorUtilities.bind(new Binder());
        serviceLocator.inject(this);
    }

    @Test
    public void testInjectionNotNull() {
        assertNotNull(documentationFromCodeGenerator);
    }

    @Test
    public void testDocumentationFromCodeGenerator() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        PushEvent pushEvent = mapper.readValue(JSON_REQUEST, PushEvent.class);
        assertNotNull(pushEvent);
        //documentationFromCodeGenerator.process(pushEvent);
    }
}
