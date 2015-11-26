package com.networkedassets.autodoc.transformer.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contains settings for one space
 */
public class ConfluenceSettings implements Serializable {

    private Map<String, Project> projects = new HashMap<>();
    private String confluenceUrl = "";

    /**
     * Do not add projects using <code>getProjects().add(...)</code>, use {@link ConfluenceSettings#addProject(Project)}.
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


    public Map<String, Project> getProjectsMap() {
        return projects;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("projects", projects)
                .add("confluenceUrl", confluenceUrl)
                .toString();
    }

    public String getConfluenceUrl() {
        return confluenceUrl;
    }

    public void setConfluenceUrl(String confluenceUrl) {
        this.confluenceUrl = confluenceUrl;
    }
}
