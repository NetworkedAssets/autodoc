package com.networkedassets.autodoc.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networkedassets.autodoc.transformer.handleRepoPush.PushEvent;
import com.networkedassets.autodoc.transformer.handleRepoPush.provide.in.PushEventProcessor;
import com.networkedassets.autodoc.transformer.manageSettings.provide.out.SettingsProvider;
import com.networkedassets.autodoc.transformer.server.Binder;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.inject.Inject;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by mtulaza on 2015-12-07.
 */
@Category(IntegrationTest.class)
public class DocumentationFromCodeGeneratorIntegrationTest {
    @Inject
    private PushEventProcessor documentationFromCodeGenerator;
    @Inject
    private SettingsProvider settingsProvider;

    private final String JSON_REQUEST = "{\n" +
            "\"sourceUrl\" : \"http://46.101.240.138:7990/\",\n" +
            "\"projectKey\" : \"APD\",\n" +
            "\"repositorySlug\" : \"javadoc-plugin\",\n" +
            "\"branchId\" : \"master\"\n" +
            "}";

    private PushEvent createPushEventInstanceFromJSON() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(JSON_REQUEST, PushEvent.class);
    }

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
        assertNotNull(settingsProvider);
    }

    @Test
    public void testConvertJSONtoObject() throws IOException {
        assertNotNull(createPushEventInstanceFromJSON());
    }

    @Test
    @Ignore
    public void testDocumentationFromCodeGenerator() throws IOException {
        documentationFromCodeGenerator.process(createPushEventInstanceFromJSON());
    }

    @Test
    public void testJSONSourceUrlExists() throws IOException {
        final String sourceUrl = createPushEventInstanceFromJSON().getSourceUrl();
        assertNotNull(sourceUrl);
        assertNotEquals(sourceUrl, "");

        //assertTrue(settingsProvider.getCurrentSettings().isSourceWithUrlExistent(sourceUrl));
    }
}
