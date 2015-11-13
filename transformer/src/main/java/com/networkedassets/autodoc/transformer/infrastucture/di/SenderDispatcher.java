package com.networkedassets.autodoc.transformer.infrastucture.di;

import com.networkedassets.autodoc.transformer.usecases.boundary.provide.DocGenerator;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.DocSender;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.DocSenderFactory;

public class SenderDispatcher implements DocSenderFactory {

	@Override
	public DocSender createDocumentationSender(DocGenerator docGenerator) {
		return null;
	}

}
