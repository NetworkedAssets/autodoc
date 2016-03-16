package com.networkedassets.autodoc.integration.documentation;

import org.junit.Test;

/**
 * Created by Kamil on 29.02.2016.
 */
public class PostIntegrationTest extends DocumentationServiceIntegrationTest {

    @Test
    public void testPostDocumentationForProject() {
        postDocumentationPiece().then().assertThat().statusCode(200);
    }

}
