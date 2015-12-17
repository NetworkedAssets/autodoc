package com.networkedassets.autodoc.transformer.manageSettings.provide.in;

import com.networkedassets.autodoc.transformer.settings.Source;

/**
 *  Adds source to the system and returns the same source, but with modified verification flags
 */
public interface SourceCreator {
    Source addSource(Source source);
}
