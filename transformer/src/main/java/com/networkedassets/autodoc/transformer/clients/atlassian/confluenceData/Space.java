package com.networkedassets.autodoc.transformer.clients.atlassian.confluenceData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by mrobakowski on 9/28/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Space {
    private String key;

    public Space() {
    }

    public Space(String spaceKey) {
        key = spaceKey;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "Space{" +
                "key='" + key + '\'' +
                '}';
    }
}
