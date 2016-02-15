package com.networkedassets.autodoc.transformer.handleRepoPush.core;


public class DefaultDocumentationGeneratorFactory implements DocumentationGeneratorFactory {
    @Override
    public DocumentationGenerator createFor(DocumentationType type) {
        switch (type) {
            case JAVADOC:
                return new JavadocGenerator();
            case UML:
                return new UmlGenerator();
            default:
                return null;
        }
    }
}
