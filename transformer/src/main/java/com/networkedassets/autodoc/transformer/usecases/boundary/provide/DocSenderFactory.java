package com.networkedassets.autodoc.transformer.usecases.boundary.provide;

public interface DocSenderFactory {

	DocSender createDocumentationSender(DocGenerator docGenerator);
}
