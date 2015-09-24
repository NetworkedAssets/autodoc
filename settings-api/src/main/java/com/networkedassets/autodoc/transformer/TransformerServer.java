package com.networkedassets.autodoc.transformer;

import com.networkedassets.autodoc.transformer.settings.SettingsException;
import com.networkedassets.autodoc.transformer.settings.SettingsForSpace;

/**
 * This interface is implemented for all the backends for Autodoc configuration screen. It provides means to get the
 * necessary info from the backend and report changes to the backend
 *
 * @author mrobakowski
 */
public interface TransformerServer {
    SettingsForSpace getSettingsForSpace(String spaceKey) throws SettingsException;
    void saveSettingsForSpace(SettingsForSpace settings, String spaceKey) throws SettingsException;
}
