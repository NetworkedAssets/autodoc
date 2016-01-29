package com.networkedassets.autodoc.transformer.handleRepoPush.core;


@FunctionalInterface
public interface DocumentationGeneratorFactory {
    DocumentationGenerator createFor(DocumentationType type);
}
