package com.networkedassets.autodoc.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networkedassets.autodoc.transformer.handleRepoPush.Code;
import com.networkedassets.autodoc.transformer.handleRepoPush.PushEvent;
import com.networkedassets.autodoc.transformer.handleRepoPush.core.JavadocGenerator;
import com.networkedassets.autodoc.transformer.handleRepoPush.require.CodeProvider;
import com.networkedassets.autodoc.transformer.manageSettings.provide.out.SettingsProvider;
import com.networkedassets.autodoc.transformer.server.Binder;
import com.networkedassets.autodoc.transformer.settings.Source;
import org.eclipse.jgit.util.StringUtils;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.inject.Inject;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Created by mtulaza on 2015-12-09.
 */
@Category(IntegrationTest.class)
public class JavadocGeneratorIntegrationTest {
    @Inject
    private CodeProvider gitCodeProvider;
    @Inject
    private SettingsProvider settingsProvider;

    @Before
    public void supplyInjections() {
        ServiceLocator serviceLocator = ServiceLocatorUtilities.bind(new Binder());
        serviceLocator.inject(this);
    }

    private final String JSON_REQUEST = "{\n" +
            "\"sourceUrl\" : \"http://46.101.240.138:7990\",\n" +
            "\"projectKey\" : \"APD\",\n" +
            "\"repositorySlug\" : \"javadoc-plugin\",\n" +
            "\"branchId\" : \"refs/heads/master\"\n" +
            "}";

    private PushEvent createPushEventInstanceFromJSON() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(JSON_REQUEST, PushEvent.class);
    }

    @Test
    public void testInjectionsNotNull() {
        assertNotNull(gitCodeProvider);
    }

    @Test
    public void testGenerateFromCode() throws IOException {
        PushEvent pushEvent = createPushEventInstanceFromJSON();
        final String sourceUrl = pushEvent.getSourceUrl();
        final String projectKey = pushEvent.getProjectKey();
        final String repoSlug = pushEvent.getRepositorySlug();
        final String branchId = pushEvent.getBranchId();
        assertFalse(StringUtils.isEmptyOrNull(sourceUrl));

        Source source = settingsProvider.getCurrentSettings().getSourceByUrl(sourceUrl);
        Code code = gitCodeProvider.getCode(source, projectKey, repoSlug, branchId);
        JavadocGenerator generator = new JavadocGenerator();

        Assert.assertNotNull(generator.generateFrom(code));
    }

    @Test (expected = NullPointerException.class)
    public void testGenerateFromCodeGivenNullThrowsNullPointerException() throws IOException {
        JavadocGenerator generator = new JavadocGenerator();

        generator.generateFrom(null);
    }
}
