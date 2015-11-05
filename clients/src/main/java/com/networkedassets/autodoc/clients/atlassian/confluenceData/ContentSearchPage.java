package com.networkedassets.autodoc.clients.atlassian.confluenceData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Iterator;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentSearchPage implements Iterable<ConfluencePage> {
    private List<ConfluencePage> results;
    private Links _links;

    public List<ConfluencePage> getResults() {
        return results;
    }

    public void setResults(List<ConfluencePage> results) {
        this.results = results;
    }

    public Links getLinks() {
        return _links;
    }

    public void setLinks(Links _links) {
        this._links = _links;
    }

    @Override
    public String toString() {
        return "ContentSearchPage{" +
                "results=" + results +
                ", _links=" + _links +
                '}';
    }

    @Override
    public Iterator<ConfluencePage> iterator() {
        return results.iterator();
    }
}
