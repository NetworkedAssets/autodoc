package com.networkedassets.autodoc.transformer.manageSettings.provide.out;

import com.networkedassets.autodoc.transformer.settings.ConfluenceSettings;
import com.networkedassets.autodoc.transformer.settings.TransformerSettings;

import java.util.Collection;

/**
 * Created by mrobakowski on 11/12/2015.
 */
public interface SettingsProvider {
    ConfluenceSettings getSettingsForSpace(String spaceKey, String confluenceUrl);
    Collection<ConfluenceSettings> getSettingsForSpaces();
    TransformerSettings getTransformerSettings();
}
