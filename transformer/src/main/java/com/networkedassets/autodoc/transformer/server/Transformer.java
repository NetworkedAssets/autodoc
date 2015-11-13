package com.networkedassets.autodoc.transformer.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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
                Application.class.getCanonicalName()
        );


        try {
            jettyServer.start();
            jettyServer.join();
        } finally {
            jettyServer.destroy();
        }
    }
}
