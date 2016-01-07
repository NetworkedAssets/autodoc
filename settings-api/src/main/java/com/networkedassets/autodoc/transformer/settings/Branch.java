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
	public String displayId = "!!NO_NAME!!";
	public String id = "!!NO_NAME!!";
	private ListenType listenTo;
	public List<ScheduledEvent> scheduledEvents;

	public Branch() {

		scheduledEvents = new ArrayList<>();
	}

	public Branch(String displayId, String id) {
		this();
		this.displayId = displayId;
		this.id = id;
	}

	public Branch(Branch otherBranch) {
		this(otherBranch.displayId, otherBranch.id);
		this.listenTo = otherBranch.listenTo;
		this.scheduledEvents = new ArrayList<>(otherBranch.scheduledEvents);
	}

	@JsonGetter
	public ListenType getListenTo() {
		return this.listenTo==null?ListenType.none:listenTo;
	}
	
	@JsonSetter
	public void setListenTo(ListenType listenTo) {
		this.listenTo = listenTo;
	}

	public enum ListenType implements Serializable {
		none, git, schedule;

		public String getListenTypeId() {
			return this.toString();
		}
	}

}
