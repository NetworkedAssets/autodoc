package com.networkedassets.autodoc.transformer.server;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

/**
 * Jersey application serverConfig
 */
@SuppressWarnings("unused")
@ApplicationPath("/")
public class Application extends ResourceConfig {
	public Application() {

		// scan for classes providing REST services
		packages(true, "com.networkedassets.autodoc.transformer");

		// enable jersey logging for requests and responses
		property("com.sun.jersey.spi.container.ContainerRequestFilters",
				"com.sun.jersey.api.container.filter.LoggingFilter");
		property("com.sun.jersey.spi.container.ContainerResponseFilters",
				"com.sun.jersey.api.container.filter.LoggingFilter");
		property(ServerProperties.TRACING, "ALL");

		// register binder for dependency injection
		register(new Binder());
		register(JacksonFeature.class);
		register(JacksonJsonProvider.class);

	}

}
