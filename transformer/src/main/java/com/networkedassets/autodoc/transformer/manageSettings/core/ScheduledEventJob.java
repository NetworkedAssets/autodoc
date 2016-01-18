package com.networkedassets.autodoc.transformer.manageSettings.core;

import com.networkedassets.autodoc.transformer.handleRepoPush.core.DefaultDocumentationGeneratorFactory;
import com.networkedassets.autodoc.transformer.handleRepoPush.core.DocumentationFromCodeGenerator;
import com.networkedassets.autodoc.transformer.handleRepoPush.infrastructure.ConfluenceDocumentationSender;
import com.networkedassets.autodoc.transformer.handleRepoPush.infrastructure.GitCodeProvider;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by mgilewicz on 2016-01-14.
 */
public class ScheduledEventJob implements Job {
    private String sourceUrl;
    private String projectKey;
    private String repoSlug;
    private String branchId;

    public ScheduledEventJob() {
    }
    public void execute(JobExecutionContext context)
            throws JobExecutionException
    {
        SettingsManager settingsManager = new SettingsManager();
        DefaultDocumentationGeneratorFactory docFactory = new DefaultDocumentationGeneratorFactory();
        ConfluenceDocumentationSender sender = new ConfluenceDocumentationSender();
        GitCodeProvider codeProvider = new GitCodeProvider();
        DocumentationFromCodeGenerator docGen = new DocumentationFromCodeGenerator(settingsManager, docFactory, sender,
                codeProvider);

        docGen.generateDocumentation(sourceUrl, projectKey, repoSlug, branchId);
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public void setRepoSlug(String repoSlug) {
        this.repoSlug = repoSlug;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }
}