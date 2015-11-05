package com.networkedassets.autodoc.clients.atlassian.confluenceData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Links {
    private String next;

    @Override
    public String toString() {
        return "Links{" +
                "next='" + next + '\'' +
                '}';
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }
}
