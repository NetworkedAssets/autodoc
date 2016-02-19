package com.networkedassets.autodoc.transformer.util;

import com.networkedassets.autodoc.transformer.settings.ScheduledEvent;
import org.quartz.CronScheduleBuilder;

import java.util.Map;
import java.util.Set;

/**
 * Utils producing CRON (time-based job) used at scheduling events.
 */
public class ScheduledEventHelper {

    /**
     * Creates CRON for given event.
     * Syntax: sec min hour day_of_month month day_of_week year(optional)
     * Example: "0 0/5 * * * ?" - fire every 5 minutes
     *
     * @param event Event containing full firing-time description
     * @return CRON built from event
     */
    public static CronScheduleBuilder getCronSchedule(ScheduledEvent event) {
        String[] splitTime = event.getTime().split(":");
        String h = splitTime[0];
        String min = splitTime[1];

        return CronScheduleBuilder.cronSchedule("00 " + min + " " + h + " " + getCronDays(event));
    }

    private static String getCronDays(ScheduledEvent event) {
        String date;
        if (event.isPeriodic()) {
            //Every week
            if (event.getPeriodType()== ScheduledEvent.PeriodType.WEEK) {
                String days = "";
                Set<Map.Entry<String, Object>> weekdays = event.getWeekdays().entrySet();

                for (Map.Entry<String, Object> entry : weekdays) {
                    if ((Boolean) entry.getValue())
                        days += entry.getKey().toUpperCase() + ",";
                }
                if (days.isEmpty())
                    days = "*";
                else //Remove comma after last day
                    days = days.substring(0, days.length() - 1);

                date = "? * " + days;
            } else //Every x days
                date = "*/" + event.getNumber() + " * ?";
        } else
            date = event.getDay() + " "
                    + event.getMonth() + " ? "
                    + event.getYear();

        return date;
    }
}
