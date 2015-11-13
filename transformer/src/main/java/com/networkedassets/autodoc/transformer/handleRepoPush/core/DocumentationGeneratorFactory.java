package com.networkedassets.autodoc.transformer.handleRepoPush.core;

/**
 * Created by mrobakowski on 11/12/2015.
 */
@FunctionalInterface
public interface DocumentationGeneratorFactory {
    DocumentationGenerator createFor(DocumentationType type);
}
