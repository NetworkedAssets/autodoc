package com.networkedassets.autodoc.transformer.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains settings for one space
 */
public class SettingsForSpace implements Serializable {

    private List<Project> projects = new ArrayList<>();
    private ConfluenceSpace confluenceSpace = new ConfluenceSpace();

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
        return confluenceSpace.getSpaceKey();
    }

    public void setSpaceKey(String spaceKey) {
        confluenceSpace.setSpaceKey(spaceKey);
    }

    public String getConfluenceUrl() {
        return confluenceSpace.getConfluenceUrl();
    }

    public void setConfluenceUrl(String confluenceUrl) {
        confluenceSpace.setConfluenceUrl(confluenceUrl);
    }

    public ConfluenceSpace getConfluenceSpace() {
        return confluenceSpace;
    }

    public void setConfluenceSpace(ConfluenceSpace confluenceSpace) {
        this.confluenceSpace = confluenceSpace;
    }
}
