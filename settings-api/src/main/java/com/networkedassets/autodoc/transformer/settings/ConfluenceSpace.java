package com.networkedassets.autodoc.transformer.settings;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a confluence space
 */
public class ConfluenceSpace implements Serializable {
    private String spaceKey = "";
    private String confluenceUrl = "";

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfluenceSpace that = (ConfluenceSpace) o;
        return Objects.equals(spaceKey, that.spaceKey) &&
                Objects.equals(confluenceUrl, that.confluenceUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spaceKey, confluenceUrl);
    }
}
