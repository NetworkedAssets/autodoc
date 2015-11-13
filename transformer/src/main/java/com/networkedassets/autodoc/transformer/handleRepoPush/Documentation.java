package com.networkedassets.autodoc.transformer.handleRepoPush;

/**
 * Created by mrobakowski on 11/12/2015.
 */
public class Documentation {
    // TODO: probably make this class an abstract class and have general structure for Javadoc and UML in JavadocDocumentation and UmlDocumentation classes
    private String documentationData;

    public Documentation(String documentationData) {
        this.documentationData = documentationData;
    }

    public String getDocumentationData() {
        return documentationData;
    }

    public void setDocumentationData(String documentationData) {
        this.documentationData = documentationData;
    }
}
