package com.networkedassets.autodoc.integration.infrastructure;

import com.google.common.collect.Lists;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.networkedassets.autodoc.integration.IntegrationTest;
import com.networkedassets.autodoc.transformer.settings.Credentials;
import com.networkedassets.autodoc.transformer.settings.Settings;
import com.networkedassets.autodoc.transformer.settings.TransformerSettings;
import com.networkedassets.autodoc.transformer.util.PropertyHandler;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

@Category(IntegrationTest.class)
public class SettingsServiceIntegrationTest {
    private final static String CONFLUENCE_URL = "http://atlas.networkedassets.net/confluence";
    private static String TRANSFORMER_URL;
    private static int TRANSFORMER_PORT;
    private final RequestSpecification HTTPSvalidatedRequest = given()
            .relaxedHTTPSValidation();

    @BeforeClass
    public static void setupRestAssured() {
        RestAssured.baseURI = TransformerConstants.getBaseUri();
        RestAssured.port =TransformerConstants.getPort();
        RestAssured.basePath = "/settings";

        TRANSFORMER_URL = cutAllSlashes(PropertyHandler.getInstance().getValue("jetty.address"));
        TRANSFORMER_PORT = Integer.parseInt(PropertyHandler.getInstance().getValue("jetty.port"));
    }

    @Test
    public void testGETSettingsEndpoint() {
        HTTPSvalidatedRequest
        .when()
                .get()
        .then()
                .assertThat().contentType(ContentType.JSON)
                .statusCode(200)
                .body("credentials.confluenceUsername", equalTo("admin"))
                .body("credentials.confluencePassword", nullValue())
                .body("confluenceUrl", equalTo(CONFLUENCE_URL))
                .body("transformerSettings.address", equalTo(TRANSFORMER_URL + ":" + TRANSFORMER_PORT + "/event"));
    }

    @Test
    public void testGETCredentialsEndpoint() {
        HTTPSvalidatedRequest
        .when()
                .get("/credentials")
        .then()
                .assertThat().contentType(ContentType.JSON)
                .statusCode(200)
                .body("confluenceUsername", equalTo("admin"))
                .body("confluencePassword", nullValue());
    }

    @Test
    public void testPOSTCredentialsEndpoint() {
        HTTPSvalidatedRequest
                .contentType(ContentType.JSON)
                .body(buildSettingsObject())
        .when()
                .post("/credentials")
        .then()
                .statusCode(202);
    }

    private Settings buildSettingsObject() {
        Settings settings = new Settings();

        Credentials credentials = new Credentials();
        credentials.setConfluenceUsername("admin");
        credentials.setConfluencePassword("admin");

        settings.setCredentials(credentials);
        settings.setConfluenceUrl(CONFLUENCE_URL);

        TransformerSettings transformerSettings = new TransformerSettings();
        transformerSettings.setAddress(TRANSFORMER_URL, TRANSFORMER_PORT);

        settings.setTransformerSettings(transformerSettings);
        settings.setSources(Lists.newArrayList());

        return settings;
    }

    private static String cutAllSlashes(String url) {
        return (url.endsWith("/") || url.endsWith("\\")) ? cutAllSlashes(url.substring(0, url.length() - 1)) : url;
    }
}
