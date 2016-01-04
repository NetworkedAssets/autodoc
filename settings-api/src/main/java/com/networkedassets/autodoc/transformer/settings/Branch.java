package com.networkedassets.autodoc.transformer.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

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

	public static enum ListenType implements Serializable {
		none("none"), git("git"), schedule("schedule");

		private String listenTypeId;

		ListenType(String listenTypeId) {
			this.listenTypeId = listenTypeId;
		}

		public String getListenTypeId() {

			return this.listenTypeId;
		}

		public void setListenTypeId(String listenTypeId) {
			this.listenTypeId = listenTypeId;
		}

	}

}
