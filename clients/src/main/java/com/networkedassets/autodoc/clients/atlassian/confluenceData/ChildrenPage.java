package com.networkedassets.autodoc.clients.atlassian.confluenceData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Joiner;

import java.util.List;

/**
 * Created by mrobakowski on 9/28/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChildrenPage {
    public List<ConfluencePage> results;

    public List<ConfluencePage> getResults() {
        return results;
    }

    public void setResults(List<ConfluencePage> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "ChildrenPage{" +
                "results=" + Joiner.on(", ").join(results) +
                '}';
    }
}

