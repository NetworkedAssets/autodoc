package com.networkedassets.autodoc.integration.documentation;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import org.junit.BeforeClass;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.preemptive;

public class DocumentationServiceIntegrationTest {

    public static final String project = "TEST";
    public static final String repo = "testrepo";
    public static final String branch = "refs%252Fheads%252Fmaster";
    public static final String body = "Lorem ipsum dolor sit amet";
    public static final String docPieceName = "DPN";
    public static final String docType = "javadoc";
    public static final String pieceType = "pieceTypename";



    @BeforeClass
    public static void setupRestAssured() {
        RestAssured.baseURI = "http://atlas.networkedassets.net";
        RestAssured.basePath = "/confluence/rest/doc/1.0/documentation";
        RestAssured.authentication = preemptive().basic("admin", "admin");
    }

    protected Response postDocumentationPiece() {
        return given().queryParam("pieceType", pieceType)
                .body(body)
                .contentType(ContentType.JSON)
                .post(String.format("/%s/%s/%s/%s/%s",project,repo,branch,docType,docPieceName))
                .andReturn();
    }

}

