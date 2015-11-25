package com.networkedassets.autodoc.transformer.server;

import com.networkedassets.autodoc.transformer.util.PropertyHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 */
public class Transformer {

    static final Logger log = LoggerFactory.getLogger(Transformer.class);
    private static Server jettyServer;


    public static void main(String[] args) throws Exception {

        jettyServer = getServer(Integer.parseInt(PropertyHandler.getInstance().getValue("jetty.port", "8050")));
        try {
            jettyServer.start();
            jettyServer.join();
        } finally {
            jettyServer.destroy();
        }
    }


    public static Server getServer(int port) {
        if (jettyServer != null) {
            return jettyServer;
        } else {

            Server newJettyServer = new Server(port);

            ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
            newJettyServer.setHandler(servletContextHandler);


            ServletHolder jerseyServlet = servletContextHandler.addServlet(
                    org.glassfish.jersey.servlet.ServletContainer.class, "/*"
            );
            jerseyServlet.setInitOrder(0);

            jerseyServlet.setInitParameter(
                    "javax.ws.rs.Application",
                    Application.class.getCanonicalName()
            );
            jettyServer = newJettyServer;
            return newJettyServer;
        }
    }
}
