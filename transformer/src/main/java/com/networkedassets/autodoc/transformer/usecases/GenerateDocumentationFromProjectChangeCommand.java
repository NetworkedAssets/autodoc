package com.networkedassets.autodoc.transformer.usecases;

import java.util.Collection;
import java.util.EnumSet;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.networkedassets.autodoc.transformer.settings.SettingsForSpace;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.Command;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.DocGenerator;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.DocGeneratorFactory;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.DocSender;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.DocSenderFactory;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.DocType;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.Event;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.SettingsProvider;

public class GenerateDocumentationFromProjectChangeCommand implements Command {
	@SuppressWarnings("unused")
	public static Logger log = LoggerFactory.getLogger(GenerateDocumentationFromProjectChangeCommand.class);

	private SettingsProvider settingsProvider;
	private DocGeneratorFactory docGeneratorFactory;
	private DocSenderFactory docSenderFactory;
	private Event projectChangeEvent;

	@Inject
	public GenerateDocumentationFromProjectChangeCommand(SettingsProvider settingsProvider,
			DocGeneratorFactory docGeneratorFactory, DocSenderFactory docSenderFactory,
			Event projectChangeEvent) {
		this.settingsProvider = settingsProvider;
		this.docGeneratorFactory = docGeneratorFactory;
		this.docSenderFactory = docSenderFactory;
		this.projectChangeEvent = projectChangeEvent;

	}

	@Override
	public void execute() {
		String projectKey = projectChangeEvent.getProjectKey();
		String repoSlug = projectChangeEvent.getRepositorySlug();
		String branchId = projectChangeEvent.getBranchId();

		Collection<SettingsForSpace> interestedSpaces = settingsProvider.getSettingsForSpaces().stream()
				.filter(s -> s.getProjectByKey(projectKey).getRepoBySlug(repoSlug).getBranchById(branchId).isListened)
				.collect(Collectors.toList());

		EnumSet.allOf(DocType.class).forEach(docType -> {
			DocGenerator docGenerator = docGeneratorFactory.createDocumentationGenerator(docType, projectKey,
					repoSlug, branchId, interestedSpaces);
			DocSender docSender = docSenderFactory.createDocumentationSender(docGenerator);
			docSender.send();
		});

	}
}
