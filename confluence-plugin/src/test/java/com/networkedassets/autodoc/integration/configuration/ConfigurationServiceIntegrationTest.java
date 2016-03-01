package com.networkedassets.autodoc.integration.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.networkedassets.autodoc.integration.IntegrationTest;
import com.networkedassets.autodoc.transformer.settings.Branch;
import com.networkedassets.autodoc.transformer.settings.Credentials;
import com.networkedassets.autodoc.transformer.settings.Source;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
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
    }

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

    @Test
    public void testPUTModifyBranchEndpoint() throws UnsupportedEncodingException {
        final int sourceId = 1;
        final String projectKey = "CAT";
        final String repoSlug = "catrepo";
        final String branchId = "refs/heads/master";
        final String PUTurl = "/sources/" + sourceId + "/" + enc(projectKey) + "/" + enc(repoSlug) + "/" + enc(branchId);

        Branch sampleBranch = ConfigurationServiceTestHelper
                .getSampleBranch(sourceId, projectKey, repoSlug, branchId);
        Branch.ListenType originalListenType = sampleBranch.getListenTo();
        Branch.ListenType testListenType = Branch.ListenType.git;


        //modifyBranchListenType(sampleBranch, null, PUTurl);
        modifyBranchListenType(sampleBranch, testListenType, PUTurl)
                .then()
                .assertThat().contentType(ContentType.JSON)
                .statusCode(200)
                .body("listenTo", equalTo(testListenType));

        modifyBranchListenType(sampleBranch, originalListenType, PUTurl)
                .then()
                .assertThat().contentType(ContentType.JSON)
                .statusCode(200)
                .body("listenTo", equalTo(originalListenType));
    }

    public Response modifyBranchListenType(Branch branch, Branch.ListenType listenType, String PUTurl) {
        branch.setListenTo(listenType);
        final Response response =
            given()
                .contentType(ContentType.JSON)
                .body(branch)
            .when()
                .put(PUTurl);

        return response;
    }

    @Test
    public void testPOSTSetCredentialsEndpoint() {
        Credentials credentials = new Credentials();
        credentials.setConfluenceUsername("kcala");
        credentials.setConfluencePassword("admin");

        setCredentialsPOST(credentials);

        credentials.setConfluenceUsername("admin");
        setCredentialsPOST(credentials);
    }

    private void setCredentialsPOST(Credentials credentials) {
        given()
                .contentType(ContentType.JSON)
                .body(credentials)
        .when()
                .post("/credentials")
        .then()
                .statusCode(204)
                .body(isEmptyString());
    }

    @Test
    public void testGETgetCredentialsEndpoint() {
        when()
                .get("/credentials")
        .then()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .body("confluencePassword", nullValue())
                .body("confluenceUsername", notNullValue());
    }

    @Test
    public void testPOSTsetSourcesFromAppLinksEndpoint() {
        given()
                .header("X-Atlassian-Token", "no-check")
        .when()
                .post("/applinks/sources")
        .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("sources.size()", greaterThanOrEqualTo(1));
    }

    @Test
    public void testGETgetListenedBranchesEndpoint() {
        when()
                .get("/branches/listened")
        .then()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .body("sources.size()", greaterThanOrEqualTo(1));
    }

    @Test
    public void testPOSTforceGenerateEndpoint() throws UnsupportedEncodingException {
        final String sourceUrl = "http://atlas.networkedassets.net:7990";
        final String projectKey = "CAT";
        final String repoSlug = "catrepo";
        final String branchId = "refs/heads/master";

        given()
                .header("X-Atlassian-Token", "no-check")
        .when()
                .post("/event/" + enc(sourceUrl) + "/" + enc(projectKey) + "/" + enc(repoSlug) + "/" + enc(branchId))
        .then()
                .statusCode(anyOf(equalTo(200), equalTo(202)))
                .body(isEmptyString());
    }

    @Test
    public void testGETgetSourcesEndpoint() {
        when()
                .get("/sources")
        .then()
                .assertThat().contentType(ContentType.JSON)
                .statusCode(200)
                .body(not(empty()))
                .body("size()", greaterThanOrEqualTo(1))
                .body("size()", greaterThanOrEqualTo(1))
                .body("get(0).id", notNullValue())
                .body("get(0).name", notNullValue())
                .body("get(0).url", notNullValue())
                .body("get(0).sourceType", notNullValue())
                .body("get(0).username", notNullValue())
                .body("get(0).sourceExists", equalTo(true))
                .body("get(0).credentialsCorrect", equalTo(true))
                .body("get(0).nameCorrect", equalTo(true))
                .body("get(0).sourceTypeCorrect", equalTo(true))
                .body("get(0).appLinksId", notNullValue())
                .body("get(0).hookKey", notNullValue());
    }

    @Test
    public void testGETgetExtendedSourcesFilteredByCurrentUserEndpoint() {
        when()
                .get("/sources/extended")
        .then()
                .assertThat().contentType(ContentType.JSON)
                .statusCode(200)
                .body("projects", notNullValue())
                .body("projects.size()", greaterThanOrEqualTo(1));
    }

    @Test
    @Ignore // test should work after ignoring unknown properties
    public void testPOSTandDELETEremoveSourceEndpoint() throws IOException {
        Source toAddSource = getSampleSource();
        final String testSourceName = "INTEGRATION_TEST_SOURCE_NAME";
        toAddSource.setName(testSourceName);

        Response post =
        given()
                .contentType(ContentType.JSON)
                .body(toAddSource)
        .when()
                .post("/sources");

        post.then()
                .assertThat().contentType(ContentType.JSON)
                .statusCode(201);

        Source sourceFromResponse = post.getBody().as(Source.class);
        Assert.assertThat(sourceFromResponse.getName(), equalTo(testSourceName));

        when()
                .delete("/sources/" + sourceFromResponse.getId())
        .then()
                .statusCode(202)
                .body(isEmptyString());
    }

    @Test
    @Ignore // test should work after ignoring unknown properties
    public void testPUTsetSourceEndpoint() throws IOException {
        Source source = getSampleSource();
        final String originalSourceName = source.getName();
        final String testSourceName = "INTEGRATION_TEST_SOURCE_NAME";
        final int sourceId = source.getId();

        sendPUTsetSource(source, testSourceName, sourceId).then()
                .assertThat().contentType(ContentType.JSON)
                .statusCode(200)
                .body("name", equalTo(testSourceName));

        sendPUTsetSource(source, originalSourceName, sourceId).then()
                .assertThat().contentType(ContentType.JSON)
                .statusCode(200)
                .body("name", equalTo(originalSourceName));

    }

    private Response sendPUTsetSource(Source source, String testSourceName, int sourceId) {
        source.setName(testSourceName);
        return given()
                .contentType(ContentType.JSON)
                .body(source)
        .when()
                .put("/sources/" + sourceId);
    }

    private Source getSampleSource() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<Source> sources = mapper.readValue(get("/sources").asString(), new TypeReference<List<Source>>(){});

        Assert.assertThat(sources, notNullValue());
        Assert.assertThat(sources.get(0), notNullValue());
        return sources.get(0);
    }

    private String enc(String toEncodeUTF8) throws UnsupportedEncodingException {
        return URLEncoder.encode(toEncodeUTF8, "UTF-8");
    }
}
