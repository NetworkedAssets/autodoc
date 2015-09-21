package com.networkedassets.autodoc.transformer.settings;

import com.atlassian.fugue.Pair;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by mrobakowski on 9/17/2015.
 */
public class Branch {
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

    public Map<String, ?> toSoyData() {
        return ImmutableMap.<String, Object>builder()
                .put("displayId", this.displayId)
                .put("id", this.id)
                .put("javadocPageId", this.javadocPageId)
                .put("umlPageId", this.umlPageId)
                .put("listenedEvents", listenedEventsAsSoy())
                .put("scheduledEvents", scheduledEvents.stream().map(ScheduledEvent::toSoyData).collect(Collectors.toList()))
                .build();
    }


    private Map<String, Boolean> listenedEventsAsSoy() {
        return this.listenedEvents.entrySet().stream()
                .map(e -> new Pair<>(e.getKey().eventName, e.getValue()))
                .collect(Collectors.toMap(Pair::left, Pair::right));
    }
}
