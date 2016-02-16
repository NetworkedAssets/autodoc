package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;

import javax.inject.Inject;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.networkedassets.autodoc.transformer.handleRepoPush.PushEvent;
import com.networkedassets.autodoc.transformer.handleRepoPush.provide.in.PushEventProcessor;

public class ScheduledEventJob implements Job {
    private String sourceUrl;
    private String projectKey;
    private String repoSlug;
    private String branchId;

    private PushEventProcessor eventProcessor;

    @Inject
    public ScheduledEventJob(PushEventProcessor eventProcessor) {
        this.eventProcessor = eventProcessor;
    }
    public void execute(JobExecutionContext context)
            throws JobExecutionException
    {
        PushEvent event = new PushEvent();
        event.setBranchId(branchId);
        event.setProjectKey(projectKey);
        event.setRepositorySlug(repoSlug);
        event.setSourceUrl(sourceUrl);

        eventProcessor.processEvent(event);
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