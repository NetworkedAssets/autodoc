package com.networkedassets.autodoc.integration;

import com.github.markusbernhardt.xmldoclet.xjc.Root;
import com.networkedassets.autodoc.transformer.util.javadoc.Javadoc;
import com.networkedassets.autodoc.transformer.util.javadoc.JavadocException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by mgilewicz on 2015-12-11.
 */
@Category(IntegrationTest.class)
public class JavadocIntegrationTest {

    @Test
    public void testStructureFromDirectoryGivenCorrectPathReturnsRoot() throws JavadocException, IOException {
        Root result = Javadoc.structureFromDirectory(Paths.get(System.getProperty("user.dir")));

        Assert.assertNotNull(result);
    }

    @Test (expected = NullPointerException.class)
    public void testStructureFromDirectoryGivenNullPathThrowsNullPointerException() throws JavadocException, IOException{
        Javadoc.structureFromDirectory(null);
    }

}
