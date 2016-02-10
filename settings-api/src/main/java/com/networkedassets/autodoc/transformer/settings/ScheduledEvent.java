package com.networkedassets.autodoc.transformer.settings;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;



/**
 * Class representing a scheduled event - an event fired at particular times
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduledEvent implements Serializable {

    private static final long serialVersionUID = -1213178165118904796L;

    private boolean periodic;
    private PeriodType periodType;
    private int number;
    private HashMap<String, Boolean> weekdays;
    private Date oneTimeDate;
    private String time;

    public ScheduledEvent() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public boolean isPeriodic() {
        return periodic;
    }

    public void setPeriodic(boolean periodic) {
        this.periodic = periodic;
    }
    
    public PeriodType getPeriodType() {
        return periodType;
    }
   
    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public HashMap<String, Boolean> getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(HashMap<String, Boolean> weekdays) {
        this.weekdays = weekdays;
    }

    public Date getOneTimeDate() {
        return oneTimeDate;
    }

    public void setOneTimeDate(Date oneTimeDate) {
        this.oneTimeDate = oneTimeDate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public enum PeriodType {
        DAY, WEEK
    }

    @Override
    public String toString() {
        return "ScheduledEvent{" +
                "periodic=" + periodic +
                ", periodType=" + periodType +
                ", number=" + number +
                ", weekdays=" + weekdays +
                ", oneTimeDate=" + oneTimeDate +
                ", time='" + time + '\'' +
                '}';
    }
}
