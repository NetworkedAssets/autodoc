package com.networkedassets.autodoc.transformer.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Calendar;
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
    private HashMap<String, Object> weekdays;
    private String time;
    private String year;
    private String month;
    private String day;
    private Date oneTimeDate;

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

    public HashMap<String, Object> getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(HashMap<String, Object> weekdays) {
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

    public int getDay(){
        return Integer.parseInt(day);
    }

    public int getMonth(){
        return Integer.parseInt(month);
    }

    public int getYear(){
        return Integer.parseInt(year);
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setDay(String day) {
        this.day = day;
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
