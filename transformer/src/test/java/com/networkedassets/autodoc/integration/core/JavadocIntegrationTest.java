package com.networkedassets.autodoc.integration.core;

import com.networkedassets.autodoc.integration.IntegrationTest;
import com.networkedassets.autodoc.jsondoclet.model.Root;
import com.networkedassets.autodoc.transformer.util.javadoc.Javadoc;
import com.networkedassets.autodoc.transformer.util.javadoc.JavadocException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.nio.file.Paths;

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
