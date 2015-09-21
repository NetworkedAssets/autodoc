package com.networkedassets.autodoc.transformer.settings;

import com.google.common.collect.ImmutableMap;

import java.time.Instant;
import java.time.Period;
import java.util.Map;

/**
 * Created by mrobakowski on 9/17/2015.
 */
public class ScheduledEvent {
    private Instant scheduleStart;
    private Period period;

    public ScheduledEvent(Instant scheduleStart, Period period) {
        this.scheduleStart = scheduleStart;
        this.period = period;
    }

    public Instant getScheduleStart() {
        return scheduleStart;
    }

    public void setScheduleStart(Instant scheduleStart) {
        this.scheduleStart = scheduleStart;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public Map<String, String> toSoyData() {
        return ImmutableMap.of(
                "scheduleStart", scheduleStart.toString(),
                "period", period.toString()
        );
    }
}
