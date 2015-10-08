package com.networkedassets.autodoc.transformer.clients.atlassian.confluenceData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by mrobakowski on 9/28/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Children {
    public ChildrenPage page;

    @Override
    public String toString() {
        return "Children{" +
                "page=" + page +
                '}';
    }

    public ChildrenPage getPage() {
        return page;
    }

    public void setPage(ChildrenPage page) {
        this.page = page;
    }
}
