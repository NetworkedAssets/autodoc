package com.networkedassets.autodoc.transformer.handleRepoPush.core;

import com.networkedassets.autodoc.transformer.handleRepoPush.Code;
import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.handleRepoPush.PushEvent;
import com.networkedassets.autodoc.transformer.handleRepoPush.provide.in.PushEventProcessor;
import com.networkedassets.autodoc.transformer.handleRepoPush.require.CodeProvider;
import com.networkedassets.autodoc.transformer.handleRepoPush.require.DocumentationSender;
import com.networkedassets.autodoc.transformer.manageSettings.provide.out.SettingsProvider;
import com.networkedassets.autodoc.transformer.settings.Branch.ListenType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

@SuppressWarnings("unused")
public class DocumentationFromCodeGenerator implements PushEventProcessor {
	@SuppressWarnings("unused")
	public static Logger log = LoggerFactory.getLogger(DocumentationFromCodeGenerator.class);

	private SettingsProvider settingsProvider;
	private DocumentationGeneratorFactory docGeneratorFactory;
	private DocumentationSender documentationSender;
	private CodeProvider codeProvider;

	@Inject
	public DocumentationFromCodeGenerator(SettingsProvider settingsProvider,
			DocumentationGeneratorFactory docGeneratorFactory, DocumentationSender documentationSender,
			CodeProvider codeProvider) {
		this.settingsProvider = settingsProvider;
		this.docGeneratorFactory = docGeneratorFactory;
		this.documentationSender = documentationSender;
		this.codeProvider = codeProvider;
	}

	@Override
	public void process(PushEvent pushEvent) {
		String sourceUrl = pushEvent.getSourceUrl();
		String projectKey = pushEvent.getProjectKey();
		String repoSlug = pushEvent.getRepositorySlug();
		String branchId = pushEvent.getBranchId();

		if (settingsProvider.getCurrentSettings().isSourceWithUrlExistent(sourceUrl) || !settingsProvider
				.getCurrentSettings().getSourceByUrl(sourceUrl).getProjectByKey(projectKey).getRepoBySlug(repoSlug)
				.getBranchById(branchId).getListenTo().equals(ListenType.none)) {

			Code code = codeProvider.getCode(settingsProvider.getCurrentSettings().getSourceByUrl(sourceUrl),
					projectKey, repoSlug, branchId);

			for (DocumentationType docType : DocumentationType.values()) {
				Documentation documentation = docGeneratorFactory.createFor(docType).generateFrom(code);
				documentation.setProjectInfo(projectKey, repoSlug, branchId);
				documentationSender.send(documentation, settingsProvider.getCurrentSettings());
			}
		}
	}
}
