package com.networkedassets.autodoc.transformer.settings;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contains settings for one space
 */
public class SettingsForSpace implements Serializable {

    private Map<String, Project> projects = new HashMap<>();
    private ConfluenceSpace confluenceSpace = new ConfluenceSpace();

    /**
     * Do not add projects using <code>getProjects().add(...)</code>, use {@link SettingsForSpace#addProject(Project)}.
     * However, you can remove elements
     */
    public Collection<Project> getProjects() {
        return projects.values();
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects.stream().collect(Collectors.toMap(p -> p.key, p -> p));
    }

    public void addProject(Project p) {
        projects.put(p.key, p);
    }

    public Project getProjectByKey(String key) {
        return projects.get(key);
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
