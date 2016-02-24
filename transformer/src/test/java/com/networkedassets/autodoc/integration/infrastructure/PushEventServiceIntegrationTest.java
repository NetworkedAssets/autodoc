package com.networkedassets.autodoc.integration.infrastructure;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.networkedassets.autodoc.integration.IntegrationTest;
import com.networkedassets.autodoc.transformer.handleRepoPush.PushEvent;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static com.jayway.restassured.RestAssured.given;

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
    @Ignore //AsyncResponse needs to be tested
    public void test() {
        HTTPSvalidatedRequest
                .contentType(ContentType.JSON)
                .body(buildPushEvent())
        .when()
                .post("/event")
        .then()
                .statusCode(202);
    }

    private PushEvent buildPushEvent() {
        PushEvent event = new PushEvent();
        event.setBranchId("refs/heads/master");
        event.setProjectKey("APD");
        event.setRepositorySlug("javadoc-plugin");
        event.setSourceUrl("http://atlas.networkedassets.net:7990");
        return event;
    }
}
