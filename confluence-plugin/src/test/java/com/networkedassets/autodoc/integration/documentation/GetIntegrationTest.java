package com.networkedassets.autodoc.integration.documentation;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static com.jayway.restassured.RestAssured.when;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.containsString;

public class GetIntegrationTest extends DocumentationServiceIntegrationTest {

    @Before
    public void setUp() throws Exception {
        postDocumentationPiece();
    }

    @Test
    public void testGetDocumentationPiece() {
        when().get(String.format("/%s/%s/%s/%s/%s", project, repo, branch, docType, docPieceName))
                .then().assertThat().statusCode(200).body(containsString(body));
    }

    @Test
    public void testGetDocumentationPiecesForProject() throws Exception {
        String json = when().get(String.format("/%s/%s/%s/%s", project, repo, branch, docType)).asString();
        List<HashMap<String, String>> docPieces = from(json).get("documentationPieces");
        Assert.assertTrue(docPieces.stream()
                .anyMatch(h -> h.containsKey("type") && h.containsKey("name")
                        && h.get("type").equals(pieceType) && h.get("name").equals(docPieceName)));
    }

    @Test
    public void testSearchDocumentation() {
        final String lorem = when().get(
                String.format("/%s/%s/%s/%s/search?q=%s",
                        project,
                        repo,
                        branch,
                        docType,
                        "Lorem"))
                .asString();
        Assert.assertTrue(lorem.contains(docPieceName));
    }

    @Test
    public void testGetDocumentationPieceByAttribute() throws Exception {
        final String dolor = when().get(String.format("/%s/%s/%s/%s/%s/%s",
                project,
                repo,
                branch,
                docType,
                docPieceName,
                "dolor")).asString();
        final String sit_amet = from(dolor).get("dolor");

        Assert.assertEquals(sit_amet, "sit amet");
    }
}
