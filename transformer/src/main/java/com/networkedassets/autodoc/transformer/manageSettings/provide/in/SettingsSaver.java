package com.networkedassets.autodoc.transformer.manageSettings.provide.in;

import com.networkedassets.autodoc.transformer.settings.Settings;
import org.jvnet.hk2.annotations.Service;

/**
 * Created by mrobakowski on 11/12/2015.
 */
public interface SettingsSaver {
    void setCurrentSettings(Settings settings);
}
