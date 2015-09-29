package com.networkedassets.autodoc.transformer.clients.atlassian.stashData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.MoreObjects;

@JsonIgnoreProperties({"metadata"})
public class Branch {
    private String id;
    private String displayId;
    private String latestChangeset;
    private String latestCommit;
    private boolean isDefault;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayId() {
        return displayId;
    }

    public void setDisplayId(String displayId) {
        this.displayId = displayId;
    }

    public String getLatestChangeset() {
        return latestChangeset;
    }

    public void setLatestChangeset(String latestChangeset) {
        this.latestChangeset = latestChangeset;
    }

    public String getLatestCommit() {
        return latestCommit;
    }

    public void setLatestCommit(String latestCommit) {
        this.latestCommit = latestCommit;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
                .add("id", id)
                .add("displayId", displayId)
                .add("latestChangeset", latestChangeset)
                .add("latestCommit", latestCommit)
                .add("isDefault", isDefault)
                .toString();
    }
}
