package com.networkedassets.autodoc.transformer.settings;

import java.util.List;

/**
 * Contains settings of the application
 */
public class Settings implements TransformerSettings {

    List<Project> projects;

    @Override
    public List<Project> getProjectsStateForSpace(String spaceKey) {
        return null;
    }

    @Override
    public void setProjectsStateForSpace(List<Project> projects, String spaceKey) {

    }
}
