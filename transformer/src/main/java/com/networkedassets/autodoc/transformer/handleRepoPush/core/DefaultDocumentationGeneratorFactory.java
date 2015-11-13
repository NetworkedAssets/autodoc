package com.networkedassets.autodoc.transformer.handleRepoPush.core;

/**
 * Created by mrobakowski on 11/13/2015.
 */
public class DefaultDocumentationGeneratorFactory implements DocumentationGeneratorFactory {
    @Override
    public DocumentationGenerator createFor(DocumentationType type) {
        switch (type) {
            case JAVADOC:
                return new JavadocGenerator();
            case UML_CLASS_DIAGRAM:
                return new UmlGenerator();
            default:
                return null;
        }
    }
}
