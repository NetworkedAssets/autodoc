package com.networkedassets.autodoc.transformer.server;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;

import javax.ws.rs.ApplicationPath;

/**
 * Jersey application serverConfig
 */

@ApplicationPath("/")
public class Application extends ResourceConfig {
	private static ServiceLocator serviceLocator;

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

		register(new ContainerLifecycleListener() {
			@Override
			public void onStartup(Container container) {
				serviceLocator = container.getApplicationHandler().getServiceLocator();
			}

			@Override
			public void onReload(Container container) {

			}

			@Override
			public void onShutdown(Container container) {

			}
		});

	}

	public static ServiceLocator getServiceLocator() {
		return serviceLocator;
	}

	public static <T> T getService(Class<T> cls) {
		return serviceLocator.getService(cls);
	}
}
