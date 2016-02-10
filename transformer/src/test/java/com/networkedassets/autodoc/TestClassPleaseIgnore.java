package com.networkedassets.autodoc;

import com.networkedassets.autodoc.transformer.handleRepoPush.Code;
import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.handleRepoPush.core.DefaultDocumentationGeneratorFactory;
import com.networkedassets.autodoc.transformer.handleRepoPush.core.DocumentationGeneratorFactory;
import com.networkedassets.autodoc.transformer.handleRepoPush.core.DocumentationType;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Paths;

public class TestClassPleaseIgnore {
    @Ignore
    @Test
    public void test() {
        Code code = new Code(Paths.get("C:\\Users\\mrobakowski\\projects\\autodoc\\transformer\\src"));
        DocumentationGeneratorFactory dgf = new DefaultDocumentationGeneratorFactory();
        Documentation javadoc = dgf.createFor(DocumentationType.JAVADOC).generateFrom(code);
//        Documentation uml = dgf.createFor(DocumentationType.UML).generateFrom(code);
        System.out.println("success");
    }
}
