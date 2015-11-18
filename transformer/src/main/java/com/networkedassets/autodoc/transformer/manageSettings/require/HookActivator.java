package com.networkedassets.autodoc.transformer.manageSettings.require;

import com.networkedassets.autodoc.transformer.settings.Project;

import java.util.Map;

/**
 * Created by kamil on 18.11.2015.
 */
public interface HookActivator {

    void enableAllHooks(Map<String, Project> projects);
}
