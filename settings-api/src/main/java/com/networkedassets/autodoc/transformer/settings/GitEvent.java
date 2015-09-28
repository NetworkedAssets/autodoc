package com.networkedassets.autodoc.transformer.settings;

/**
 * Enum representing a git event type
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
