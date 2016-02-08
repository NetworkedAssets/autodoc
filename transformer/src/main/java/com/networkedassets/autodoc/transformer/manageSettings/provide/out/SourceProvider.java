package com.networkedassets.autodoc.transformer.manageSettings.provide.out;

import java.util.List;
import java.util.Optional;

import com.networkedassets.autodoc.transformer.settings.Source;

public interface SourceProvider {
	Optional<Source> getSourceById(int id);

	List<Source> getAllSources();
}
