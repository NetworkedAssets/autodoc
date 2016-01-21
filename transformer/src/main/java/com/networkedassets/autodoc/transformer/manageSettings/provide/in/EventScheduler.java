package com.networkedassets.autodoc.transformer.manageSettings.provide.in;

import com.networkedassets.autodoc.transformer.settings.Branch;

/**
 * Created by mgilewicz on 2016-01-20.
 */
@FunctionalInterface
public interface EventScheduler{
    void scheduleEvents(Branch currentBranch, int sourceId,
                               String projectKey, String repoSlug, String branchId);
}
