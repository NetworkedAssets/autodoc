package com.networkedassets.autodoc.transformer.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains settings for one space
 */
public class SettingsForSpace implements Serializable {

    private List<Project> projects = new ArrayList<>();
    private String spaceKey = "";
    private String confluenceUrl = "";

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public Project getProjectByKey(String key) {
        return projects.stream().filter(p -> p.key.equals(key)).findFirst().orElse(null);
    }

    public String getSpaceKey() {
        return spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public String getConfluenceUrl() {
        return confluenceUrl;
    }

    public void setConfluenceUrl(String confluenceUrl) {
        this.confluenceUrl = confluenceUrl;
    }
}
