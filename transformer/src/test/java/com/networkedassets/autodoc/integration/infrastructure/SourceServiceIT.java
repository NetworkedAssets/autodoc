package com.networkedassets.autodoc.integration.infrastructure;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ValidatableResponse;
import com.jayway.restassured.specification.RequestSpecification;
import com.networkedassets.autodoc.integration.BaseIT;
import com.networkedassets.autodoc.integration.TransformerConstants;
import com.networkedassets.autodoc.transformer.settings.Branch;
import com.networkedassets.autodoc.transformer.settings.Source;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class SourceServiceIT extends BaseIT {

    private final RequestSpecification HTTPSvalidatedRequest = given()
            .relaxedHTTPSValidation();

    @BeforeClass
    public static void setupRestAssured() {
        RestAssured.baseURI = TransformerConstants.getHost();
        RestAssured.port = TransformerConstants.getPort();
        RestAssured.basePath = TransformerConstants.getPath() + "/sources";
    }

    @Test
    public void testGETSourcesEndpoint() {
        HTTPSvalidatedRequest
        .when()
                .get()
        .then()
                .assertThat().contentType(ContentType.JSON)
                .statusCode(200)
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
    public void testGETExtendedEndpoint() {
        HTTPSvalidatedRequest
        .when()
                .get("/extended")
        .then()
                .assertThat().contentType(ContentType.JSON)
                .statusCode(200)
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
                .body("get(0).correct", equalTo(true))
                .body("get(0).projects.size()", greaterThanOrEqualTo(1)) // 0?
                .body("get(0).appLinksId", notNullValue())
                .body("get(0).hookKey", notNullValue());
    }

    @Test
    public void testGETSourceByIdEndpoint() {
        final int id = 1;

        HTTPSvalidatedRequest
        .when()
                .get("/" + id)
        .then()
                .assertThat().contentType(ContentType.JSON)
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", notNullValue())
                .body("url", notNullValue())
                .body("sourceType", notNullValue())
                .body("username", notNullValue())
                .body("sourceExists", equalTo(true))
                .body("credentialsCorrect", equalTo(true))
                .body("nameCorrect", equalTo(true))
                .body("sourceTypeCorrect", equalTo(true))
                .body("correct", equalTo(true))
                .body("projects.size()", greaterThanOrEqualTo(1)) // 0?
                .body("appLinksId", notNullValue())
                .body("hookKey", notNullValue());
    }

    @Test
    public void testPUTModifySourceByIdEndpoint() {
        final int id = 1;
        Source sampleSource = getSampleSource(1);

        String sampleName = sampleSource.getName();
        String testName = "NEW_NAME_FOR_INTEGRATION_TEST";

        sampleSource.setName(testName);
        HTTPSvalidatedRequest
                .contentType(ContentType.JSON)
                .body(sampleSource)
        .when()
                .put("/" + id)
        .then()
                .assertThat().contentType(ContentType.JSON)
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo(testName));

        // Restore previous name
        sampleSource.setName(sampleName);
        HTTPSvalidatedRequest.contentType(ContentType.JSON)
                .body(sampleSource).put("/" + id);
    }

    @Test
    public void testPOSTandDELETEAddSourceEndpoint() {
        String tempName = "INTEGRATION_TEST_SOURCE";

        Source sampleSource = getSampleSource(1);
        sampleSource.setName(tempName);

        int createdSourceId =
        HTTPSvalidatedRequest
                .contentType(ContentType.JSON)
                .body(sampleSource)
        .when()
                .post()
        .then()
                .assertThat().contentType(ContentType.JSON)
                .statusCode(201)
                .body("name", equalTo(tempName))
        .extract()
                .as(Source.class).getId();

        deleteSource(createdSourceId);
    }

    private void deleteSource(int createdSourceId) {
        HTTPSvalidatedRequest
        .when()
                .delete("/" + createdSourceId)
        .then()
                .statusCode(202);
    }

    @Test
    public void testPUTModifyBranchEndpoint() throws UnsupportedEncodingException {
        final String branchId = "refs/heads/master";
        final String projectKey = "CAT";
        final String repoSlug = "catrepo";
        final String endpoint = "/1/" + URLEncoder.encode(projectKey, "UTF-8") + "/" + URLEncoder.encode(repoSlug, "UTF-8") + "/" + URLEncoder.encode(branchId, "UTF-8");

        Branch branch = getSampleSource(1).getProjectByKey(projectKey)
                .getRepoBySlug(repoSlug).getBranchById(branchId);

        final String originalDisplayId = branch.getDisplayId();
        final String testDisplayId = "INTEGRATION_TEST_DISPLAY_ID";

        modifyOriginalBranchDisplayId(branch, testDisplayId, endpoint);
        modifyOriginalBranchDisplayId(branch, originalDisplayId, endpoint).body("displayId", equalTo(originalDisplayId));
    }

    private ValidatableResponse modifyOriginalBranchDisplayId(Branch originalBranch, String newDisplayId, String endpoint) {
        originalBranch.setDisplayId(newDisplayId);
        return HTTPSvalidatedRequest
                .contentType(ContentType.JSON)
                .body(originalBranch)
        .when()
                .put(endpoint)
        .then()
                .assertThat().contentType(ContentType.JSON)
                .statusCode(200);
    }


    public Source getSampleSource(int id) {
        Response response = HTTPSvalidatedRequest
                .when()
                .get("/" + id);
        return response.getBody().as(Source.class);
    }
}
