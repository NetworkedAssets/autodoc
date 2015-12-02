package com.networkedassets.autodoc.transformer.server;

import com.networkedassets.autodoc.transformer.handleRepoPush.infrastructure.GitCodeProvider;
import com.networkedassets.autodoc.transformer.handleRepoPush.require.CodeProvider;
import junit.framework.Assert;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Application;

import static junit.framework.Assert.*;

public class BinderTest extends JerseyTest {

    @Test
    public void invokeGitCodeProvider(){
        invoke(GitCodeProvider.class);
    }

    private <T extends CodeProvider> void invoke(Class<T> codeProvider) {
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

    @Path("/example")
    public static class Resource {
        @Inject
        private CodeProvider codeProvider;

        @GET
        @Path("/{serviceClass}")
        public Response getDynamicInvokedService() {
            return Response.ok(codeProvider.getClass().getName()).build();
        }
    }
}