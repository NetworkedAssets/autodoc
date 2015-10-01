package com.networkedassets.autodoc.transformer.settings;

import com.google.common.collect.ImmutableMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class representing a branch in a repo
 */
public class Branch implements Serializable{
    public String displayId = "!!NO_NAME!!";
    public String id = "!!NO_NAME!!";
    public String javadocPageId = "!!NO_NAME!!";
    public String umlPageId = "!!NO_NAME!!";
    public EnumMap<GitEvent, Boolean> listenedEvents;
    public List<ScheduledEvent> scheduledEvents;

    public Branch() {
        listenedEvents = new EnumMap<>(GitEvent.class);
        for (GitEvent e : GitEvent.values()) {
            listenedEvents.put(e, false);
        }
        scheduledEvents = new ArrayList<>();
    }

    public Branch(String displayId, String id) {
        this();
        this.displayId = displayId;
        this.id = id;
    }

    public Branch(Branch otherBranch) {
        this(otherBranch.displayId, otherBranch.id);
        this.javadocPageId = otherBranch.javadocPageId;
        this.umlPageId = otherBranch.umlPageId;
        this.listenedEvents = new EnumMap<>(otherBranch.listenedEvents);
        this.scheduledEvents = new ArrayList<>(otherBranch.scheduledEvents);
    }

    public Map<String, ?> toSoyData() {
        return ImmutableMap.<String, Object>builder()
                .put("displayId", this.displayId)
                .put("id", this.id)
                .put("javadocPageId", this.javadocPageId)
                .put("umlPageId", this.umlPageId)
                .put("listenedEvents", listenedEventsAsSoy())
                .put("scheduledEvents", scheduledEvents.stream()
                        .map(ScheduledEvent::toSoyData).collect(Collectors.toList()))
                .build();
    }


    private Map<String, Map<String, Object>> listenedEventsAsSoy() {
        return this.listenedEvents.entrySet().stream()
                .map(e -> new Pair<>(e.getKey().eventName,
                        ImmutableMap.<String, Object>of("val", e.getValue(), "enum", e.getKey().name())))
                .collect(Collectors.toMap(Pair::left, Pair::right));
    }

    private class Pair<LEFT, RIGHT> {
        LEFT leftMember;
        RIGHT rightMember;

        public Pair(LEFT leftMember, RIGHT rightMember) {
            this.leftMember = leftMember;
            this.rightMember = rightMember;
        }

        public LEFT left() {
            return leftMember;
        }

        public RIGHT right() {
            return rightMember;
        }

    }
}
