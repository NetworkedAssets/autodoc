package com.networkedassets.autodoc.transformer.infrastucture.di;

import java.util.Collection;

import com.networkedassets.autodoc.transformer.settings.SettingsForSpace;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.DocGenerator;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.DocGeneratorFactory;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.DocType;

public class DocGeneratorDispatcher implements DocGeneratorFactory {

	@Override
	public DocGenerator createDocumentationGenerator(DocType docType, String projectKey,
			String repoSlug, String branchId, Collection<SettingsForSpace> interestedSpaces) {
		return null;
	}

}
