package com.networkedassets.autodoc.transformer.manageSettings.provide.in;

import com.networkedassets.autodoc.transformer.settings.Branch;


public interface BranchModifier {
    Branch modifyBranch(int sourceId, String projectKey, String repoSlug, String branchId, Branch branch);
}
