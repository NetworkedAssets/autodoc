package com.networkedassets.autodoc.transformer.server;

import com.networkedassets.autodoc.transformer.util.PropertyHandler;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.X500Name;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * Hello world!
 */
public class Transformer {

    static final Logger log = LoggerFactory.getLogger(Transformer.class);
    public static final String KEY_STORE_PASSWORD = "keyStorePassword";
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

            Server newJettyServer = new Server();
            configureSsl(newJettyServer, port);

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

    private static void configureSsl(Server server, int port) {
        String keyStore = findOrGenerateKeystore();

        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(keyStore);
        sslContextFactory.setKeyStorePassword(KEY_STORE_PASSWORD);

        HttpConfiguration https_config = new HttpConfiguration();
        https_config.setSecureScheme("https");
        https_config.setSecurePort(port);
        https_config.setOutputBufferSize(32768);
        https_config.setRequestHeaderSize(8192);
        https_config.setResponseHeaderSize(8192);
        https_config.setSendServerVersion(true);
        https_config.setSendDateHeader(false);
        https_config.addCustomizer(new SecureRequestCustomizer());

        ServerConnector sslConnector = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(https_config));
        sslConnector.setPort(port);
        server.addConnector(sslConnector);
    }

    private static String findOrGenerateKeystore() {
        Path keyStorePath = Paths.get(".keystore");
        if (Files.exists(keyStorePath)) {
            return keyStorePath.toAbsolutePath().toString();
        }

        try {
            generateKeystore(keyStorePath);
        } catch (Exception e) {
            log.error("", e);
        }

        return keyStorePath.toAbsolutePath().toString();
    }

    private static void generateKeystore(Path keyStorePath) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, NoSuchProviderException, InvalidKeyException, SignatureException {

        final int keysize = 1024;
        final String commonName = "dupa";
        final String organizationalUnit = "DUPA";
        final String organization = "dupa";
        final String city = "dupa";
        final String state = "dupa";
        final String country = "DU";
        final long validity = 1096; // 3 years
        final String alias = "dupa";
        final char[] keyPass = KEY_STORE_PASSWORD.toCharArray();

        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, null);

        CertAndKeyGen keypair = new CertAndKeyGen("RSA", "SHA1WithRSA", null);

        X500Name x500Name = new X500Name(commonName, organizationalUnit, organization, city, state, country);

        keypair.generate(keysize);
        PrivateKey privKey = keypair.getPrivateKey();

        X509Certificate[] chain = new X509Certificate[1];

        chain[0] = keypair.getSelfCertificate(x500Name, new Date(), validity * 24 * 60 * 60);

        keyStore.setKeyEntry(alias, privKey, keyPass, chain);

        keyStore.store(new FileOutputStream(keyStorePath.toAbsolutePath().toString()), keyPass);

    }
}
