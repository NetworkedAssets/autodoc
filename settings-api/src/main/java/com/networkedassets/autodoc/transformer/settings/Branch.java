package com.networkedassets.autodoc.transformer.settings;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a branch in a repo
 */
public class Branch implements Serializable {

	private static final long serialVersionUID = -8481858598787329017L;
	private String displayId;
	private String id;
	private ListenType listenTo;
	private List<ScheduledEvent> scheduledEvents;

	public Branch() {
		setScheduledEvents(new ArrayList<>());
	}

	public Branch(String displayId, String id) {
		this();
		this.setDisplayId(displayId);
		this.setId(id);
	}

	public ListenType getListenTo() {
		return this.listenTo == null ? ListenType.none : listenTo;
	}

	public void setListenTo(ListenType listenTo) {
		this.listenTo = listenTo;
	}

	public String getDisplayId() {
		return displayId;
	}

	public void setDisplayId(String displayId) {
		this.displayId = displayId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<ScheduledEvent> getScheduledEvents() {
		return scheduledEvents;
	}

	@JsonSetter
	public void setScheduledEvents(List<ScheduledEvent> scheduledEvents) {
		this.scheduledEvents = scheduledEvents;
	}

	public enum ListenType implements Serializable {
		none, git, schedule;

		public String getListenTypeId() {
			return this.toString();
		}
	}

}
