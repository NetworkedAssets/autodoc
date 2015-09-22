package com.networkedassets.autodoc.transformer.configuration;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * Jersey application configuration
 */
@SuppressWarnings("unused")
public class Application extends ResourceConfig {
    public Application() {
        //scan for classes providing REST services
        packages(true, "com.networkedassets.autodoc.transformer");

        //register binder for dependency injection
        register(new Binder());

    }
}
