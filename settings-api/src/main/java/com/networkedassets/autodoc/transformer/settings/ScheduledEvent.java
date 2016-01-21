package com.networkedassets.autodoc.transformer.settings;

import org.quartz.CronScheduleBuilder;

import java.io.Serializable;
import java.util.*;

import static org.quartz.CronScheduleBuilder.cronSchedule;

/**
 * Class representing a scheduled event - an event fired at particular times
 */
public class ScheduledEvent implements Serializable {

	private static final long serialVersionUID = -1213178165118904796L;

	public void setPeriodic(boolean periodic) {
		this.periodic = periodic;
	}

	public void setType(String type) {
		this.type = EventType.valueOf(type);
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public void setWeekdays(HashMap<String,Boolean> weekdays) {
		this.weekdays = weekdays;
	}

	public void setOneTimeDate(Date oneTimeDate) {
		this.oneTimeDate = oneTimeDate;
	}

	public void setTime(String time) {
		this.time = time;
	}
//	private transient Instant scheduleStart = Instant.EPOCH;
//	private transient Period period = Period.ZERO;
//
//	private String scheduleStartIso;
//	private String periodIso;

	public enum EventType{
		DAY ("day"), WEEK("week");

		private final String type;

		EventType(String type){
			this.type = type;
		}
	}
	private boolean periodic;
	private EventType type;
	private int number;
	private HashMap<String, Boolean> weekdays;
	private Date oneTimeDate;
	private String time;

	public ScheduledEvent() {
	}

	public ScheduledEvent(boolean periodic, String type, int number,
						  HashMap<String, Boolean> weekdays, Date oneTimeDate, String time) {
		setPeriodic(periodic);
		setType(type);
		setNumber(number);
		setWeekdays(weekdays);
		setOneTimeDate(oneTimeDate);
		setTime(time);
	}

	public CronScheduleBuilder getCronSchedule(){
		String[] splitTime = time.split(":");
		String h = splitTime[0];
		String min = splitTime[1];

		return cronSchedule("0 "+min+" "+h+" " + getCronDays());
	}

	private String getCronDays() {
		String date;
		if(periodic) {
			if(type == EventType.WEEK) {
				String days = "";
				// Get a set of the entries
				Set set = weekdays.entrySet();
				// Get an iterator
				Iterator i = set.iterator();
				// Display elements
				while (i.hasNext()) {
					Map.Entry me = (Map.Entry) i.next();
					if ((Boolean) me.getValue())
						days += ((String) me.getKey()).toUpperCase() + ",";
				}
				if (days.isEmpty())
					days = "*";
				else
					days = days.substring(0, days.length() - 1);

				date = "* * "+days+" *";
			} else
				date = "*/" + number + " * *";
		}
		else
			date = oneTimeDate.getDay()+ " " + oneTimeDate.getMonth() + " * " + oneTimeDate.getYear();
		return date;
	}

//	public ScheduledEvent(Instant scheduleStart, Period period) {
//		setScheduleStart(scheduleStart);
//		setPeriod(period);
//	}

//	@JsonIgnore
//	public Instant getScheduleStart() {
//		return scheduleStart;
//	}
//
//	@JsonIgnore
//	public void setScheduleStart(Instant scheduleStart) {
//		this.scheduleStart = scheduleStart;
//		scheduleStartIso = scheduleStart.toString();
//	}
//
//	@JsonIgnore
//	public Period getPeriod() {
//		return period;
//	}
//
//	@JsonIgnore
//	public void setPeriod(Period period) {
//		this.period = period;
//		periodIso = period.toString();
//	}
//
//	public Map<String, String> toSoyData() {
//		return ImmutableMap.of("scheduleStartIso", getScheduleStartIso(), "periodIso", getPeriodIso());
//	}
//
//	public String getScheduleStartIso() {
//		if (scheduleStartIso == null) {
//			if (scheduleStart == null)
//				return "ERROR";
//			scheduleStartIso = scheduleStart.toString();
//		}
//		return scheduleStartIso;
//	}
//
//	public void setScheduleStartIso(String scheduleStartIso) {
//		scheduleStart = Instant.parse(scheduleStartIso);
//		this.scheduleStartIso = scheduleStartIso;
//	}
//
//	public String getPeriodIso() {
//		if (periodIso == null) {
//			if (period == null)
//				return "ERROR";
//			periodIso = period.toString();
//		}
//		return periodIso;
//	}
//
//	public void setPeriodIso(String periodIso) {
//		period = Period.parse(periodIso);
//		this.periodIso = periodIso;
//	}
}
