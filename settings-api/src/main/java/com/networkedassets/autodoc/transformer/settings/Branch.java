package com.networkedassets.autodoc.transformer.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a branch in a repo
 */
public class Branch implements Serializable{
    public String displayId = "!!NO_NAME!!";
    public String id = "!!NO_NAME!!";
    public boolean isListened;
    public List<ScheduledEvent> scheduledEvents;

    public Branch() {

        scheduledEvents = new ArrayList<>();
    }

    public Branch(String displayId, String id) {
        this();
        this.displayId = displayId;
        this.id = id;
    }

    public Branch(Branch otherBranch) {
        this(otherBranch.displayId, otherBranch.id);
        this.isListened = otherBranch.isListened;
        this.scheduledEvents = new ArrayList<>(otherBranch.scheduledEvents);
    }

   
}
