package com.networkedassets.autodoc.transformer.clients.atlassian.data;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Links {

	@JsonProperty("clone")
	private List<CloneLink> clone = new ArrayList<CloneLink>();
	@JsonProperty("self")
	private List<SelfLink> self = new ArrayList<SelfLink>();

	@JsonProperty("clone")
	public List<CloneLink> getClone() {
		return clone;
	}

	@JsonProperty("clone")
	public void setClone(List<CloneLink> clone) {
		this.clone = clone;
	}

	@JsonProperty("self")
	public List<SelfLink> getSelf() {
		return self;
	}

	@JsonProperty("self")
	public void setSelf(List<SelfLink> self) {
		this.self = self;
	}

}
