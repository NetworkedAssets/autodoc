package com.networkedassets.autodoc.transformer.server;

import com.networkedassets.autodoc.transformer.handleRepoPush.core.DefaultDocumentationGeneratorFactory;
import com.networkedassets.autodoc.transformer.handleRepoPush.core.DocumentationFromCodeGenerator;
import com.networkedassets.autodoc.transformer.handleRepoPush.infrastructure.GitCodeProvider;
import com.networkedassets.autodoc.transformer.handleRepoPush.require.CodeProvider;
import com.networkedassets.autodoc.transformer.manageSettings.core.SettingsManager;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static junit.framework.Assert.assertEquals;

/**
 * Testing dependency injection
 */
public class BinderTest extends JerseyTest {

    @Test
    public void invokeGitCodeProvider() {
        invoke(GitCodeProvider.class);
    }

    @Test
    public void invokeSettingsManager() {
        invoke(SettingsManager.class);
    }

    @Test
    public void invokeDefaultDocumentationGeneratorFactory() {
        invoke(DefaultDocumentationGeneratorFactory.class);
    }

    @Test
    public void invokeDocumentationFromCodeGenerator() {
        invoke(DocumentationFromCodeGenerator.class);
    }

    /**
     * invokes RESTful resource to obtain class name injected in Resource class and compare its equality
     * @param codeProvider
     */
    private void invoke(Class codeProvider) {
        final String codeProviderName = codeProvider.getName();
        Response response = target("example/" + codeProviderName).request().get();
        assertEquals(codeProviderName, response.readEntity(String.class));
    }

    @Override
    protected Application configure(){
        return new CustomResourceConfig();
    }

    private class CustomResourceConfig extends ResourceConfig {
        public CustomResourceConfig() {
            register(new Binder());
            register(Resource.class);
        }
    }

    /**
     * RESTful resource using to @Inject different classes and compare class name passed in URI to injected class name
     */
    @Path("/example")
    public static class Resource {
        @Inject
        private CodeProvider codeProvider;
        @Inject
        private SettingsManager settingsManager;
        @Inject
        private DefaultDocumentationGeneratorFactory defaultDocumentationGeneratorFactory;
        @Inject
        private DocumentationFromCodeGenerator documentationFromCodeGenerator;

        @GET
        @Path("/{serviceClass}")
        public Response getDynamicInvokedService(@PathParam("serviceClass") String serviceClass) {
            String injectedServiceClassName = "";

            switch(serviceClass){
                case "com.networkedassets.autodoc.transformer.handleRepoPush.infrastructure.GitCodeProvider":
                    injectedServiceClassName = codeProvider.getClass().getName();
                    break;
                case "com.networkedassets.autodoc.transformer.manageSettings.core.SettingsManager":
                    injectedServiceClassName = settingsManager.getClass().getName();
                    break;
                case "com.networkedassets.autodoc.transformer.handleRepoPush.core.DefaultDocumentationGeneratorFactory":
                    injectedServiceClassName = defaultDocumentationGeneratorFactory.getClass().getName();
                    break;
                case "com.networkedassets.autodoc.transformer.handleRepoPush.core.DocumentationFromCodeGenerator":
                    injectedServiceClassName = documentationFromCodeGenerator.getClass().getName();
                    break;
            }
            return Response.ok(injectedServiceClassName).build();
        }
    }
}