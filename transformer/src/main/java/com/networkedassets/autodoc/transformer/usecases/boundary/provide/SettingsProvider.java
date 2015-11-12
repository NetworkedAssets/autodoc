package com.networkedassets.autodoc.transformer.usecases.boundary.provide;

import java.util.List;

import com.networkedassets.autodoc.transformer.settings.SettingsForSpace;

/**
 * Created by mrobakowski on 11/6/2015.
 */
@FunctionalInterface
public interface SettingsProvider {
    List<SettingsForSpace> getSettingsForSpaces() ;
}
