package com.networkedassets.autodoc.transformer.handleRepoPush.core;

import com.networkedassets.autodoc.transformer.handleRepoPush.Code;
import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.handleRepoPush.PushEvent;
import com.networkedassets.autodoc.transformer.handleRepoPush.provide.in.PushEventProcessor;
import com.networkedassets.autodoc.transformer.handleRepoPush.require.CodeProvider;
import com.networkedassets.autodoc.transformer.handleRepoPush.require.DocumentationSender;
import com.networkedassets.autodoc.transformer.manageSettings.provide.out.SettingsProvider;
import com.networkedassets.autodoc.transformer.settings.Branch.ListenType;
import com.networkedassets.autodoc.transformer.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Arrays;

public class DocumentationFromCodeGenerator implements PushEventProcessor {
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
    public void processEvent(PushEvent pushEvent) {
        String sourceUrl = pushEvent.getSourceUrl();
        String projectKey = pushEvent.getProjectKey();
        String repoSlug = pushEvent.getRepositorySlug();
        String branchId = pushEvent.getBranchId();

        generateDocumentation(sourceUrl, projectKey, repoSlug, branchId);
    }

    public void generateDocumentation(String sourceUrl, String projectKey, String repoSlug, String branchId) {
        final Settings currentSettings = settingsProvider.getCurrentSettings();

        if (doesSourceExistAndBranchIsListened(sourceUrl, projectKey, repoSlug, branchId, currentSettings)) {

            Code code = codeProvider.getCode(currentSettings.getSourceByUrl(sourceUrl),
                    projectKey, repoSlug, branchId);

            if (!sendAllDocumentationTypes(projectKey, repoSlug, branchId, currentSettings, code)) {
                log.error("Documentation not sent!");
            }
        }
    }

    /**
     * Sends all the documentation types.
     * SHORT-CIRCUITS ON FIRST FAILURE! That should be ok though, because failure usually means that Counfluence url
     * isn't set.
     *
     * @return weather operation succeeded
     */
    private boolean sendAllDocumentationTypes(String projectKey, String repoSlug, String branchId, Settings currentSettings, Code code) {
        return Arrays.asList(DocumentationType.values()).stream().allMatch(docType -> {
            Documentation documentation = docGeneratorFactory.createFor(docType).generateFrom(code);
            documentation.setProjectInfo(projectKey, repoSlug, branchId);
            return documentationSender.send(documentation, currentSettings);
        });
    }

    private boolean doesSourceExistAndBranchIsListened(String sourceUrl, String projectKey, String repoSlug,
                                                       String branchId, Settings currentSettings) {
        return currentSettings.isSourceWithUrlExistent(sourceUrl)
                || !currentSettings.getSourceByUrl(sourceUrl).getProjectByKey(projectKey)
                .getRepoBySlug(repoSlug).getBranchById(branchId).getListenTo().equals(ListenType.none);
    }
}
