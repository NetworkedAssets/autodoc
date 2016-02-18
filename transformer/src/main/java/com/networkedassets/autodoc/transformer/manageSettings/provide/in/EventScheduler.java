package com.networkedassets.autodoc.transformer.manageSettings.provide.in;

import com.networkedassets.autodoc.transformer.settings.Branch;

@FunctionalInterface
public interface EventScheduler{

    void scheduleEvents(Branch currentBranch, int sourceId,
                               String projectKey, String repoSlug, String branchId);
}
