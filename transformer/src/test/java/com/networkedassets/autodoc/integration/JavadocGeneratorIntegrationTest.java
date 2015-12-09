package com.networkedassets.autodoc.integration;

import com.networkedassets.autodoc.transformer.handleRepoPush.Code;
import com.networkedassets.autodoc.transformer.handleRepoPush.require.CodeProvider;
import com.networkedassets.autodoc.transformer.server.Binder;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

/**
 * Created by mtulaza on 2015-12-09.
 */

//TODO: add @Category(IntegrationTest.class) after merging with develop branch
public class JavadocGeneratorIntegrationTest {
    @Inject
    private CodeProvider gitCodeProvider;

    @Before
    public void supplyInjections() {
        ServiceLocator serviceLocator = ServiceLocatorUtilities.bind(new Binder());
        serviceLocator.inject(this);
    }

    @Test
    public void testInjectionsNotNull() {
        assertNotNull(gitCodeProvider);
    }

    @Test
    public void testGenerateFromCode() {
        Code code = gitCodeProvider.getCode("AUT", "autodoc", "refs/heads/master");
    }
}
