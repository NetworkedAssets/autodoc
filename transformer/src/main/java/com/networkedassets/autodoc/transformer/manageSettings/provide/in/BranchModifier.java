package com.networkedassets.autodoc.transformer.manageSettings.provide.in;

import com.networkedassets.autodoc.transformer.settings.Branch;

/**
 * Created by kamil on 07.01.2016.
 */
public interface BranchModifier {
    Branch modifyBranch(int sourceId, String projectKey, String repoSlug, String branchId, Branch branch);
}
