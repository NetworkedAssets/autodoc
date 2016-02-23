package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;

import com.networkedassets.autodoc.transformer.handleRepoPush.PushEvent;
import com.networkedassets.autodoc.transformer.handleRepoPush.provide.in.PushEventProcessor;
import com.networkedassets.autodoc.transformer.manageSettings.provide.out.SettingsProvider;
import com.networkedassets.autodoc.transformer.server.Application;
import com.networkedassets.autodoc.transformer.settings.Branch;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ScheduledEventJob implements Job {
    private static Logger log = LoggerFactory.getLogger(ScheduledEventJob.class);

    private String sourceUrl;
    private String projectKey;
    private String repoSlug;
    private String branchId;
    private String latestCommit;
    private PushEventProcessor pushEventProcessor;
    private SettingsProvider settingsProvider;

    public ScheduledEventJob() {
        pushEventProcessor = Objects.requireNonNull(Application.getService(PushEventProcessor.class));
        settingsProvider = Objects.requireNonNull(Application.getService(SettingsProvider.class));
    }

    public void execute(JobExecutionContext context)
            throws JobExecutionException
    {
        boolean firstRun = context.getPreviousFireTime() == null;

        Branch branch = settingsProvider.getCurrentSettings()
                .getSourceByUrl(sourceUrl).getProjectByKey(projectKey)
                .getRepoBySlug(repoSlug).getBranchById(branchId);

        String latestCommit = branch.getLatestCommit();

        if(firstRun || !latestCommit.equals(this.latestCommit)) {
            context.getJobDetail().getJobDataMap().put("latestCommit", latestCommit);

            PushEvent event = new PushEvent();
            event.setBranchId(branchId);
            event.setProjectKey(projectKey);
            event.setRepositorySlug(repoSlug);
            event.setSourceUrl(sourceUrl);

            log.debug("Starting to process {}", this);
            pushEventProcessor.processEvent(event);
        } else{
            log.debug("Aborted processing {}. Nothing changed on the branch since last schedule time.", this);
        }
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

    public void setLatestCommit(String latestCommit) { this.latestCommit = latestCommit; }

    @Override
    public String toString(){
        return "Scheduled event job for: " +
            sourceUrl + "/" + projectKey + "/" + repoSlug + "/" + branchId;
    }
}