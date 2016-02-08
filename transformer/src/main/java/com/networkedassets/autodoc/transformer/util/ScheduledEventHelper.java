package com.networkedassets.autodoc.transformer.util;

import com.networkedassets.autodoc.transformer.settings.ScheduledEvent;
import org.quartz.CronScheduleBuilder;

import java.util.Map;
import java.util.Set;


public class ScheduledEventHelper {
    public static CronScheduleBuilder getCronSchedule(ScheduledEvent event) {
        String[] splitTime = event.getTime().split(":");
        String h = splitTime[0];
        String min = splitTime[1];

        return CronScheduleBuilder.cronSchedule("0 " + min + " " + h + " " + getCronDays(event));
    }

    private static String getCronDays(ScheduledEvent event) {
        String date;
        if (event.isPeriodic()) {
            if (event.getPeriodType()== ScheduledEvent.PeriodType.WEEK) {
                String days = "";
                // Get a set of the entries
                Set<Map.Entry<String, Boolean>> set = event.getWeekdays().entrySet();
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
                date = "*/" + event.getNumber() + " * *";
        } else
            date = event.getOneTimeDate().getDay() + " " + event.getOneTimeDate().getMonth() + " * " + event.getOneTimeDate().getYear();
        return date;
    }
}
