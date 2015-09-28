package com.networkedassets.autodoc.transformer.clients.atlassian.confluenceData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by mrobakowski on 9/28/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Metadata {
    public Labels labels;

    @Override
    public String toString() {
        return "Metadata{" +
                "labels=" + labels +
                '}';
    }
}
