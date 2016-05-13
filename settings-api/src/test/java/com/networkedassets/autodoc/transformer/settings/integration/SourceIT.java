package com.networkedassets.autodoc.transformer.settings.integration;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ValidatableResponse;
import com.jayway.restassured.specification.RequestSpecification;
import com.networkedassets.autodoc.transformer.settings.Source;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

@Ignore
public class SourceIT {
    public static Logger log = LoggerFactory.getLogger(SourceIT.class);
    private final RequestSpecification HTTPSvalidatedRequest = given()
            .relaxedHTTPSValidation()
            .header(new Header("Authorization", "Basic YWRtaW46YWRtaW4="));

    @BeforeClass
    public static void setupRestAssured() {
        RestAssured.baseURI = "http://atlassian-na-dev-01.networkedassets.local/";
    }

    @Test
    public void checkIfHookKeyNameIsSameInSourceTypeEnumAndBitbucketLocalServer() {
        log.info("Request to bitbucket: @GET hooks");
        Response resp = HTTPSvalidatedRequest
        .when()
                .get("bitbucket/rest/api/latest/projects/DOC/repos/doc/settings/hooks/")
        .then()
                .assertThat().contentType(ContentType.JSON)
                .statusCode(200).extract().response();
        int valuesSize = resp.body().path("values.size()");
        log.info("Size of 'values' array: " + valuesSize);
        log.info("Looking for right hook..");
        for(int i = 0; i < valuesSize; i++) {
            String foundKey = resp.body().path("values.get(" + i + ").details.key");
            String foundName = resp.body().path("values.get(" + i + ").details.name");
            log.info("Found key: {}  Found name: {}", foundKey, foundName);

            if(foundName.equals("Repo Event Notifier for Bitbucket Server")){
                log.info("Right name found..  " + foundName);
                assertThat(foundKey, equalTo(Source.SourceType.BITBUCKET.getHookKey()));
            }
        }
    }

    @Test
    public void checkIfHookKeyNameIsSameInSourceTypeEnumAndStashLocalServer() {
        log.info("Request to stash: @GET hooks");
        Response resp = HTTPSvalidatedRequest
                .when()
                .get("stash/rest/api/latest/projects/DOC/repos/doc/settings/hooks/")
                .then()
                .assertThat().contentType(ContentType.JSON)
                .statusCode(200).extract().response();
        int valuesSize = resp.body().path("values.size()");
        log.info("Size of 'values' array: " + valuesSize);
        log.info("Looking for right hook..");
        for(int i = 0; i < valuesSize; i++) {
            String foundKey = resp.body().path("values.get(" + i + ").details.key");
            String foundName = resp.body().path("values.get(" + i + ").details.name");
            log.info("Found key: {}  Found name: {}", foundKey, foundName);

            if(foundName.equals("Repo Event Notifier for Stash")){
                log.info("Right name found..  " + foundName);
                assertThat(foundKey, equalTo(Source.SourceType.STASH.getHookKey()));
            }
        }
    }
}
