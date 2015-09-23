package com.networkedassets.autodoc.transformer.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableMap;


import java.time.Instant;
import java.time.Period;
import java.util.Map;


/**
 * Created by mrobakowski on 9/17/2015.
 */
public class ScheduledEvent {
    private transient Instant scheduleStart;
    private transient Period period;

    private String scheduleStartIso;
    private String periodIso;

    public ScheduledEvent() {}

    public ScheduledEvent(Instant scheduleStart, Period period) {
        setScheduleStart(scheduleStart);
        setPeriod(period);
    }

    @JsonIgnore
    public Instant getScheduleStart() {
        return scheduleStart;
    }

    @JsonIgnore
    public void setScheduleStart(Instant scheduleStart) {
        this.scheduleStart = scheduleStart;
        scheduleStartIso = scheduleStart.toString();
    }

    @JsonIgnore
    public Period getPeriod() {
        return period;
    }

    @JsonIgnore
    public void setPeriod(Period period) {
        this.period = period;
        periodIso = period.toString();
    }

    public Map<String, String> toSoyData() {
        return ImmutableMap.of(
                "scheduleStartIso", scheduleStart.toString(),
                "periodIso", period.toString()
        );
    }

    public String getScheduleStartIso() {
        return scheduleStartIso;
    }

    public void setScheduleStartIso(String scheduleStartIso) {
        scheduleStart = Instant.parse(scheduleStartIso);
        this.scheduleStartIso = scheduleStartIso;
    }

    public String getPeriodIso() {
        return periodIso;
    }

    public void setPeriodIso(String periodIso) {
        period = Period.parse(periodIso);
        this.periodIso = periodIso;
    }
}
