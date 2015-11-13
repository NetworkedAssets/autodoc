package com.networkedassets.autodoc.transformer.handleRepoPush.require;

import com.networkedassets.autodoc.transformer.handleRepoPush.Code;

/**
 * Created by mrobakowski on 11/12/2015.
 */
public interface CodeProvider {

    Code getCode(String projectKey, String repoSlug, String branchId);
}
