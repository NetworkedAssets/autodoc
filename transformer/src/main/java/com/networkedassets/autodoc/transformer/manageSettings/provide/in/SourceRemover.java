package com.networkedassets.autodoc.transformer.manageSettings.provide.in;

import com.networkedassets.autodoc.transformer.settings.Source;


public interface SourceRemover {
    /**
     * Removes all sources with matching id or url
     * @param source - should contain id OR url of the source meant to be removed
     * @return true if the source was removed
     */
    boolean removeSource(Source source);
}
