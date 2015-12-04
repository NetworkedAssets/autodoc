package com.networkedassets.autodoc.transformer.handleRepoPush.require;

import com.networkedassets.autodoc.transformer.handleRepoPush.Code;
import com.networkedassets.autodoc.transformer.settings.Source;

/**
 * Created by mrobakowski on 11/12/2015.
 */
public interface CodeProvider {

    Code getCode(Source source,String projectKey, String repoSlug, String branchId);
}
