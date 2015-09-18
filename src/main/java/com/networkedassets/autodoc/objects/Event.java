package com.networkedassets.autodoc.objects;

import java.util.List;

/**
 * POJO representing event coming from Stash
 */
public class Event {
    private int projectId;
    private String projectName;
    private String projectKey;

    private int repositoryId;
    private String repositoryName;
    private String repositorySlug;

    private List<Change> changes;

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("Event { ");
        string.append("projectId:").append(projectId).append(", ");
        string.append("projectName:").append(projectName).append(", ");
        string.append("projectKey:").append(projectKey).append(", ");
        string.append("repositoryId:").append(repositoryId).append(", ");
        string.append("repositoryName:").append(repositoryName).append(", ");
        string.append("repositorySlug:").append(repositorySlug).append(", ");
        string.append("changes: [");
        for(Change change : changes){
            string.append("{");
            string.append("refId:").append(change.refId).append(", ");
            string.append("fromHash:").append(change.fromHash).append(", ");
            string.append("toHash:").append(change.toHash).append(", ");
            string.append("type:").append(change.type);
            string.append("}");
        }
        string.append("]");
        string.append("}");
        return string.toString();
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
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

    public int getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(int repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getRepositorySlug() {
        return repositorySlug;
    }

    public void setRepositorySlug(String repositorySlug) {
        this.repositorySlug = repositorySlug;
    }

    public List<Change> getChanges() {
        return changes;
    }

    public void setChanges(List<Change> changes) {
        this.changes = changes;
    }


    private class Change {
        private String refId;
        private String fromHash;
        private String toHash;
        private String type;

        public String getRefId() {
            return refId;
        }

        public void setRefId(String refId) {
            this.refId = refId;
        }

        public String getFromHash() {
            return fromHash;
        }

        public void setFromHash(String fromHash) {
            this.fromHash = fromHash;
        }

        public String getToHash() {
            return toHash;
        }

        public void setToHash(String toHash) {
            this.toHash = toHash;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }


}
