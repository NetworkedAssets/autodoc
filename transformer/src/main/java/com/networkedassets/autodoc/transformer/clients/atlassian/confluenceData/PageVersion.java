package com.networkedassets.autodoc.transformer.clients.atlassian.confluenceData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by mrobakowski on 9/28/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageVersion {
    private int number;

    public PageVersion(int number) {
        this.number = number;
    }

    public PageVersion() {
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "Version{" +
                "number=" + number +
                '}';
    }
}
