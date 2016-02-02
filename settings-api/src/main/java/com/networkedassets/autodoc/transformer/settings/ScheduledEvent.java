package com.networkedassets.autodoc.transformer.settings;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.quartz.CronScheduleBuilder;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.quartz.CronScheduleBuilder.cronSchedule;

/**
 * Class representing a scheduled event - an event fired at particular times
 */
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
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

    public ScheduledEvent(boolean periodic, String periodType, int number,
                          HashMap<String, Boolean> weekdays, Date oneTimeDate, String time) {
        setPeriodic(periodic);
        setPeriodType(periodType);
        setNumber(number);
        setWeekdays(weekdays);
        setOneTimeDate(oneTimeDate);
        setTime(time);
    }

    public void setPeriodic(boolean periodic) {
        this.periodic = periodic;
    }

    public void setPeriodType(String periodType) {
        this.periodType = PeriodType.valueOf(periodType);
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setWeekdays(HashMap<String, Boolean> weekdays) {
        this.weekdays = weekdays;
    }

    public void setOneTimeDate(Date oneTimeDate) {
        this.oneTimeDate = oneTimeDate;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public CronScheduleBuilder getCronSchedule() {
        String[] splitTime = time.split(":");
        String h = splitTime[0];
        String min = splitTime[1];

        return cronSchedule("0 " + min + " " + h + " " + getCronDays());
    }

    private String getCronDays() {
        String date;
        if (periodic) {
            if (periodType == PeriodType.WEEK) {
                String days = "";
                // Get a set of the entries
                Set<Map.Entry<String, Boolean>> set = weekdays.entrySet();
                // Get an iterator
                // Display elements
                for (Map.Entry<String, Boolean> entry : set) {
                    if (entry.getValue())
                        days += entry.getKey().toUpperCase() + ",";
                }
                if (days.isEmpty())
                    days = "*";
                else
                    days = days.substring(0, days.length() - 1);

                date = "* * " + days + " *";
            } else
                date = "*/" + number + " * *";
        } else
            date = oneTimeDate.getDay() + " " + oneTimeDate.getMonth() + " * " + oneTimeDate.getYear();
        return date;
    }

    public enum PeriodType {
        DAY("day"), WEEK("week");

        private final String type;

        PeriodType(String type) {
            this.type = type;
        }
    }
}
