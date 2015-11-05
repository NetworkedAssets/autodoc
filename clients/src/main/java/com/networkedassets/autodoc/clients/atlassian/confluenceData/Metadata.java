package com.networkedassets.autodoc.clients.atlassian.confluenceData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Metadata {
    public Labels labels;

    public Metadata(Labels labels) {
        this.labels = labels;
    }

    public Metadata() {
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "labels=" + labels +
                '}';
    }
}
