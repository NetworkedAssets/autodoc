package com.networkedassets.autodoc.transformer.manageSettings.provide.in;

import com.networkedassets.autodoc.transformer.settings.ConfluenceSettings;
import com.networkedassets.autodoc.transformer.settings.TransformerSettings;

/**
 * Created by mrobakowski on 11/12/2015.
 */
public interface SettingsSaver {
    void setSettingsForSpace(ConfluenceSettings confluenceSettings, String spaceKey, String confluenceUrl);
    void setTransformerSettings(TransformerSettings transformerSettings);
}
