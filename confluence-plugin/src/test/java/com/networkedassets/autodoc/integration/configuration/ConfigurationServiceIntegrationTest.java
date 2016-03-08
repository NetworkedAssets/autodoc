package com.networkedassets.autodoc.integration.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.clients.atlassian.atlassianProjectsData.Branch;
import com.networkedassets.autodoc.integration.IntegrationTest;
import com.networkedassets.autodoc.transformer.settings.Source;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.net.ssl.SSLContext;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Created by mtulaza on 2016-02-26.
 */
@Category(IntegrationTest.class)
public class ConfigurationServiceIntegrationTest {

    @BeforeClass
    public static void setupRestAssured() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        RestAssured.baseURI = "http://atlas.networkedassets.net";
        RestAssured.basePath = "/confluence/rest/doc/1.0/configuration";
        RestAssured.authentication = preemptive().basic("admin", "admin");

        // Unirest.setHttpClient(getConfiguredHttpClient());
    }


    // not sure if that will be needed
    /*private static CloseableHttpClient getConfiguredHttpClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        return HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom().setSocketTimeout(90000)
                        .setConnectTimeout(90000).build())
                .setHostnameVerifier(new AllowAllHostnameVerifier())
                .setSslcontext(new SSLContextBuilder().loadTrustMaterial(null, (_1, _2) -> true).build()).build();
    }*/

    @Test
    public void testGETGetCredentialsEndpoint() {
        when()
                .get("/credentials")
        .then()
                .statusCode(200)
                .assertThat().contentType(ContentType.JSON)
                .body("confluenceUsername", notNullValue())
                .body("confluencePassword", nullValue());
    }
}
