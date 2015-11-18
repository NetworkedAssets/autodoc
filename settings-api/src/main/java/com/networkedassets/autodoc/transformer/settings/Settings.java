package com.networkedassets.autodoc.transformer.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains settings of the application
 */
public class Settings implements Serializable{
    private List<SettingsForSpace> settingsForSpaces = new ArrayList<>();
    private TransformerSettings transformerSettings = new TransformerSettings();

    public List<SettingsForSpace> getSettingsForSpaces() {
        return settingsForSpaces;
    }

    public void setSettingsForSpaces(List<SettingsForSpace> settingsForSpaces) {
        this.settingsForSpaces = settingsForSpaces;
    }

    public TransformerSettings getTransformerSettings() {
        return transformerSettings;
    }

    public void setTransformerSettings(TransformerSettings transformerSettings) {
        this.transformerSettings = transformerSettings;
    }
}
