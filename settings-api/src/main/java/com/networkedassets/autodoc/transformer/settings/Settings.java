package com.networkedassets.autodoc.transformer.settings;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains settings of the application
 */
public class Settings implements TransformerSettings {

    private List<Project> projects = new ArrayList<>();

    @Override
    public List<Project> getProjectsStateForSpace(String spaceKey) {
        return null;
    }

    @Override
    public void setProjectsStateForSpace(List<Project> projects, String spaceKey) {

    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}
