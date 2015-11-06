package com.networkedassets.autodoc.transformer.usecases.boundary.provide;

import com.networkedassets.autodoc.transformer.settings.SettingsForSpace;

/**
 * Created by mrobakowski on 11/6/2015.
 */
@FunctionalInterface
public interface SettingsSetter {
    void setSettingsForSpace(SettingsForSpace settingsForSpace, String spaceKey, String confluenceUrl);
}
