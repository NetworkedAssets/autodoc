package com.networkedassets.autodoc.transformer;

import com.networkedassets.autodoc.transformer.services.EventService;
import com.networkedassets.autodoc.transformer.services.SettingsService;
import com.networkedassets.autodoc.transformer.services.TestService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Hello world!
 */
public class Transformer {

    static final Logger log = LoggerFactory.getLogger(Transformer.class);

    private static final List<String> classnames = new ArrayList<String>();

    public static void main(String[] args) throws Exception {

        Server jettyServer = new Server(8050);

        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        jettyServer.setHandler(servletContextHandler);


        ServletHolder jerseyServlet = servletContextHandler.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/*"
        );
        jerseyServlet.setInitOrder(0);

        jerseyServlet.setInitParameter(
                "javax.ws.rs.Application",
                "com.networkedassets.autodoc.transformer.configuration.Application"
        );
//        jerseyServlet.setInitParameter(
//                "com.sun.jersey.api.json.POJOMappingFeature",
//                "true"
//        );


        try {
            jettyServer.start();
            jettyServer.join();
        } finally {
            jettyServer.destroy();
        }
    }
}
