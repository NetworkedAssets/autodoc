package com.networkedassets.autodoc.transformer.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains settings of the application
 */
public class Settings implements Serializable {
    private ConfluenceSettings confluenceSettings = new ConfluenceSettings();
    private TransformerSettings transformerSettings = new TransformerSettings();


    public TransformerSettings getTransformerSettings() {
        return transformerSettings;
    }

    public void setTransformerSettings(TransformerSettings transformerSettings) {
        this.transformerSettings = transformerSettings;
    }

    public ConfluenceSettings getConfluenceSettings() {
        return confluenceSettings;
    }

    public void setConfluenceSettings(ConfluenceSettings confluenceSettings) {
        this.confluenceSettings = confluenceSettings;
    }
}
