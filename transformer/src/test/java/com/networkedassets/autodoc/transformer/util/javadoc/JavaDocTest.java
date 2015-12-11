package com.networkedassets.autodoc.transformer.util.javadoc;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class JavaDocTest {

    @Test
    public void testSearchJavaFilesGivenCorrectPathReturnsListOfPathsToJavaFiles() throws JavadocException {
        Path path = Paths.get(System.getProperty("user.dir"));
        List<Path> result = Javadoc.searchJavaFiles(path);

        Assert.assertNotNull(result);
        Assert.assertTrue(!result.isEmpty());
    }

    @Test (expected = JavadocException.class)
    public void testSearchJavaFilesGivenIncorrectPathThrowsException() throws JavadocException {
        Javadoc.searchJavaFiles(Paths.get("\\default"));
    }

    @Test (expected = NullPointerException.class)
    public void testSearchJavaFilesGivenNullPathThrowsNullPointerException() throws JavadocException {
        Javadoc.searchJavaFiles(null);
    }

}
