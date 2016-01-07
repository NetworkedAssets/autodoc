package com.networkedassets.autodoc.transformer.manageSettings.provide.out;

import com.networkedassets.autodoc.transformer.settings.Source;

import java.util.List;
import java.util.Optional;

/**
 * Created by kamil on 16.12.2015.
 */
public interface SourceProvider {
    Optional<Source> getSourceById(int id);
    List<Source> getAllSources();
}
