package com.networkedassets.autodoc.transformer.manageSettings.provide.in;

import com.networkedassets.autodoc.transformer.settings.Source;

public interface SourceCreator {
    /**
     * Verifies and adds given source to the system if it's valid
     * @param source - should have proper url poining to working code source like eg. Bitbucket, valid credentials
     *               to login, unique name and proper type
     * @return same source, but with modified verification flags
     */
    Source addSource(Source source);
}
