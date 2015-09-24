package com.networkedassets.autodoc.transformer.settings;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains settings of the application
 */
public class SettingsForSpace implements TransformerSettings {

    private List<Project> projects = new ArrayList<>();

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}
