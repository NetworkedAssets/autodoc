package com.networkedassets.autodoc.transformer.manageSettings.require;

import com.networkedassets.autodoc.transformer.settings.Settings;

public interface SettingsPersistor {
    boolean saveSettingsToFile(String filename, Settings settings);

    Settings loadSettingsFromFile(String filename);
}
