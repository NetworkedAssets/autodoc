package com.networkedassets.autodoc.integration.infrastructure;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.networkedassets.autodoc.integration.BaseIT;
import com.networkedassets.autodoc.integration.TransformerConstants;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;

public class PushEventServiceIT extends BaseIT {
    private final RequestSpecification HTTPSvalidatedRequest = given()
            .relaxedHTTPSValidation();

    @BeforeClass
    public static void setupRestAssured() {
        RestAssured.baseURI = TransformerConstants.getHost();
        RestAssured.port = TransformerConstants.getPort();
        RestAssured.baseURI = TransformerConstants.getPath();
    }

    @Test
    public void testPOSTAddEventEndpoint() {
        HTTPSvalidatedRequest
                .contentType(ContentType.JSON)
                .body(buildPushEventString())
        .when()
                .post("/event")
        .then()
                .statusCode(anyOf(equalTo(200), equalTo(202)));
    }

    private String buildPushEventString() {
        return String.format("{\"sourceUrl\":\"%s\",\"projectKey\":\"%s\",\"repositorySlug\":\"%s\",\"branchId\":\"%s\"}",
                "http://atlas.networkedassets.net:7990",
                "APD",
                "javadoc-plugin",
                "refs/heads/master");
    }
}
