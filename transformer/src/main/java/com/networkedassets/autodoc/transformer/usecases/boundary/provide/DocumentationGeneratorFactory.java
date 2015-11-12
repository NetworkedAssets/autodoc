package com.networkedassets.autodoc.transformer.usecases.boundary.provide;

import java.util.Collection;

import com.networkedassets.autodoc.transformer.settings.SettingsForSpace;

public interface DocumentationGeneratorFactory{
	
	DocumentationGenerator createDocumentationGenerator(DocumentationType docType, String projectKey, String repoSlug,
			String branchId, Collection<SettingsForSpace> interestedSpaces);
}