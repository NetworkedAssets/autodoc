package com.networkedassets.autodoc.transformer.manageSettings.provide.out;

import com.networkedassets.autodoc.transformer.settings.Settings;

public interface SettingsProvider {
    Settings getCurrentSettings();
    Settings getNotUpdatedSettings();
}
