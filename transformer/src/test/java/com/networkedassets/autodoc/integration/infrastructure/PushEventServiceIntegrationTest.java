package com.networkedassets.autodoc.integration.infrastructure;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.networkedassets.autodoc.integration.IntegrationTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;

@Category(IntegrationTest.class)
public class PushEventServiceIntegrationTest {
    private final RequestSpecification HTTPSvalidatedRequest = given()
            .relaxedHTTPSValidation();

    @BeforeClass
    public static void setupRestAssured() {
        RestAssured.baseURI = "https://atlas.networkedassets.net";
        RestAssured.port = 8050;
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
