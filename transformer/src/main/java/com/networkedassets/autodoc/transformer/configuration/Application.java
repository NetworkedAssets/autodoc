package com.networkedassets.autodoc.transformer.configuration;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * Created by kamil on 18.09.2015.
 */
public class Application extends ResourceConfig {
    public Application() {
        register(new Binder());
        packages(true, "com.networkedassets.autodoc.transformer");
    }
}
