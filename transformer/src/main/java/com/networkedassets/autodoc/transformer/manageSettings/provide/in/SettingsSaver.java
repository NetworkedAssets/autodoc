package com.networkedassets.autodoc.transformer.manageSettings.provide.in;

import com.networkedassets.autodoc.transformer.settings.SettingsForSpace;

/**
 * Created by mrobakowski on 11/12/2015.
 */
public interface SettingsSaver {
    void setSettingsForSpace(SettingsForSpace settingsForSpace, String spaceKey, String confluenceUrl);
}
