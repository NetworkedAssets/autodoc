package com.networkedassets.autodoc.transformer.util.javadoc;

import com.github.markusbernhardt.xmldoclet.xjc.Root;
import com.networkedassets.autodoc.transformer.util.PropertyHandler;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.xmlmodel.ObjectFactory;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Test
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

    @Ignore
    @Test
    public void testStuff() throws JavadocException, JAXBException {
        Root r = Javadoc.structureFromDirectory(Paths.get("C:\\Users\\mrobakowski\\projects\\autodoc\\transformer\\src\\main\\java"));
        System.out.println(marshalToString(r));
    }

    @SuppressWarnings("Duplicates")
    public static String marshalToString(Root root) throws JAXBException {
        Map<String, Object> properties = new HashMap<String, Object>(2);
        properties.put(MarshallerProperties.MEDIA_TYPE, "application/json");
        properties.put(MarshallerProperties.JSON_INCLUDE_ROOT, true);
        properties.put(MarshallerProperties.INDENT_STRING, true);
        JAXBContext contextObj = JAXBContextFactory.createContext(new Class[] { Root.class, ObjectFactory.class },
                properties);
        Marshaller marshaller = contextObj.createMarshaller();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        marshaller.marshal(root, baos);
        return baos.toString();
    }
}
