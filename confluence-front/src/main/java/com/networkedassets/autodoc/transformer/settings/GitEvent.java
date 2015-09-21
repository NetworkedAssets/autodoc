package com.networkedassets.autodoc.transformer.settings;

/**
 * Created by mrobakowski on 9/17/2015.
 */
public enum GitEvent {
    PUSH("Push"),
    PULL_REQ("Pull request"),
    MERGE("Merge");

    public String eventName;

    GitEvent(String eventName) {
        this.eventName = eventName;
    }
}
