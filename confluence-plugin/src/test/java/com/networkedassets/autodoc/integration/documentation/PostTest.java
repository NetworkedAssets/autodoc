package com.networkedassets.autodoc.integration.documentation;

import com.networkedassets.autodoc.integration.IntegrationTest;
import groovy.lang.Category;
import org.junit.Test;

/**
 * Created by Kamil on 29.02.2016.
 */
@Category(IntegrationTest.class)
public class PostTest extends DocumentationServiceIntegrationTest {

    @Test
    public void testPostDocumentationForProject() {
        postDocumentationPiece().then().assertThat().statusCode(200);
    }

}
