package com.networkedassets.autodoc.transformer.usecases.boundary.provide;

import java.util.Collection;

import com.networkedassets.autodoc.transformer.settings.SettingsForSpace;

public interface DocGeneratorFactory{
	
	DocGenerator createDocumentationGenerator(DocType docType, String projectKey, String repoSlug,
			String branchId, Collection<SettingsForSpace> interestedSpaces);
}