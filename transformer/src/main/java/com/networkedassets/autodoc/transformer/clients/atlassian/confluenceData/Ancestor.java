package com.networkedassets.autodoc.transformer.clients.atlassian.confluenceData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Ancestor {
    private String id;

    public Ancestor(String id) {
        this.id = id;
    }

    public Ancestor() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Ancestor{" +
                "id='" + id + '\'' +
                '}';
    }
}