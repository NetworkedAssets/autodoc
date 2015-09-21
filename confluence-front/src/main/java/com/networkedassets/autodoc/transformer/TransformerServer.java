package com.networkedassets.autodoc.transformer;

import com.networkedassets.autodoc.transformer.settings.TransformerSettings;

/**
 * This interface is implemented for all the backends for Autodoc configuration screen. It provides means to get the
 * necessary info from the backend and report changes to the backend
 *
 * @author mrobakowski
 */
public interface TransformerServer {
    TransformerSettings getSettings();
    void saveSettings(TransformerSettings settings);
}
