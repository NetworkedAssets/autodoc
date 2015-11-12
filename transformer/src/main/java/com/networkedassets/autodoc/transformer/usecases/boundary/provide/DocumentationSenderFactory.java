package com.networkedassets.autodoc.transformer.usecases.boundary.provide;

public interface DocumentationSenderFactory {

	DocumentationSender createDocumentationSender(DocumentationGenerator docGenerator);
}
