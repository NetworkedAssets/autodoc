package com.networkedassets.autodoc.transformer.manageSettings.provide.out;

import com.networkedassets.autodoc.transformer.settings.SettingsForSpace;

import java.util.Collection;

/**
 * Created by mrobakowski on 11/12/2015.
 */
public interface SettingsProvider {
    SettingsForSpace getSettingsForSpace(String spaceKey, String confluenceUrl);
    Collection<SettingsForSpace> getSettingsForSpaces();
}
