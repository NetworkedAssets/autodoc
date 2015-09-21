package com.networkedassets.autodoc.transformer.settings;

import java.util.List;

public interface TransformerSettings {

    List<Project> getProjectsStateForSpace(String spaceKey);

    void setProjectsStateForSpace(List<Project> projects, String spaceKey);
}
