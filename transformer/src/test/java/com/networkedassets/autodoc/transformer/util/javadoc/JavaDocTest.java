package com.networkedassets.autodoc.transformer.util.javadoc;

import com.networkedassets.autodoc.integration.IntegrationTest;
import com.networkedassets.autodoc.transformer.util.PropertyHandler;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
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

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Ignore
    @Test
    public void testFromDirectoryGivenCorrectPathReturnsPath() throws JavadocException, IOException{
        Path result = Javadoc.fromDirectory(folder.newFolder().toPath(), null, null);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.toFile().exists());
        Assert.assertTrue(result.toFile().isDirectory());
    }

    @Test (expected = NullPointerException.class)
    public void testFromDirectoryGivenNullPathThrowsNullPointerException() throws JavadocException, IOException{
        Javadoc.fromDirectory(null, null, null);
    }

    @Test @Category(IntegrationTest.class)
    public void testFromDirectoryGivenIncorrectDocletPathAndDocletClassCreatesEmptyDirectory() throws JavadocException, IOException{
        Path result = Javadoc.fromDirectory(folder.newFolder().toPath(), "abc", "def");

        Assert.assertNotNull(result);
        Assert.assertTrue(result.toFile().exists());
        Assert.assertTrue(result.toFile().isDirectory());
        Assert.assertTrue(result.toFile().length() == 0);
    }

    @Ignore
    @Test
    public void testFromDirectoryGivenCorrectDocletPathAndDocletClassCreatesDirectory() throws JavadocException, IOException{
        Path result = Javadoc.fromDirectory(folder.newFolder().toPath(),
                new File(PropertyHandler.getInstance().getValue("doclet.filename", null)).getAbsolutePath(),
                PropertyHandler.getInstance().getValue("doclet.classname", null));

        Assert.assertNotNull(result);
        Assert.assertTrue(result.toFile().exists());
        Assert.assertTrue(result.toFile().isDirectory());
        Assert.assertTrue(result.toFile().length() != 0);
    }
}
