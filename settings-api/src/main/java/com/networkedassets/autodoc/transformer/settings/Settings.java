package com.networkedassets.autodoc.transformer.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains settings of the application
 */
public class Settings {
    private List<SettingsForSpace> settingsForSpaces = new ArrayList<>();

    public List<SettingsForSpace> getSettingsForSpaces() {
        return settingsForSpaces;
    }

    public void setSettingsForSpaces(List<SettingsForSpace> settingsForSpaces) {
        this.settingsForSpaces = settingsForSpaces;
    }
}
