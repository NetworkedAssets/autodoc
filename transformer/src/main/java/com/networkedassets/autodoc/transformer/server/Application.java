package com.networkedassets.autodoc.transformer.server;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;
import org.glassfish.jersey.server.validation.ValidationConfig;
import org.glassfish.jersey.server.validation.internal.InjectingConstraintValidatorFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import javax.validation.ParameterNameProvider;
import javax.validation.Validation;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.ContextResolver;

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
		register(ValidationConfigurationContextResolver.class);
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

	public static class ValidationConfigurationContextResolver implements ContextResolver<ValidationConfig> {

		@Context
		private ResourceContext resourceContext;

		@Override
		public ValidationConfig getContext(final Class<?> type) {
			return new ValidationConfig()
					.constraintValidatorFactory(resourceContext.getResource(InjectingConstraintValidatorFactory.class))
					.parameterNameProvider(new CustomParameterNameProvider());
		}

		private class CustomParameterNameProvider implements ParameterNameProvider {

			private final ParameterNameProvider nameProvider;

			public CustomParameterNameProvider() {
				nameProvider = Validation.byDefaultProvider().configure().getDefaultParameterNameProvider();
			}

			@Override
			public List<String> getParameterNames(final Constructor<?> constructor) {
				return nameProvider.getParameterNames(constructor);
			}

			@Override
			public List<String> getParameterNames(final Method method) {

				return nameProvider.getParameterNames(method);
			}
		}
	}

}
