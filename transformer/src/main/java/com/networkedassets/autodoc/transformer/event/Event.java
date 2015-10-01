package com.networkedassets.autodoc.transformer.event;

import com.google.common.base.MoreObjects;

import java.util.List;

/**
 * Represents event coming from Stash
 */
@SuppressWarnings("unused")
public class Event {
    private String repositorySlug;
    private String projectId;
    private String repositoryName;
    private String repositoryId;
    private String projectName;
    private String projectKey;
    private List<Change> changes;

    public String getRepositorySlug() {
        return repositorySlug;
    }

    public void setRepositorySlug(String repositorySlug) {
        this.repositorySlug = repositorySlug;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public List<Change> getChanges() {
        return changes;
    }

    public void setChanges(List<Change> changes) {
        this.changes = changes;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
                .add("repositorySlug", repositorySlug)
                .add("projectId", projectId)
                .add("repositoryName", repositoryName)
                .add("repositoryId", repositoryId)
                .add("projectName", projectName)
                .add("projectKey", projectKey)
                .add("changes", changes)
                .toString();
    }
}

