package com.networkedassets.autodoc.transformer.clients.atlassian.stashData;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public class Repository {

	private Integer id;

	private Project project;

	private String scmId;

	private Boolean forkable;

	private Link link;

	private String name;

	private String state;

	private String cloneUrl;

	private Links links;

	private String slug;

	@JsonProperty("public")
	private String isPublic;

	private String statusMessage;

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScmId() {
		return scmId;
	}

	public void setScmId(String scmId) {
		this.scmId = scmId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public Boolean getForkable() {
		return forkable;
	}

	public void setForkable(Boolean forkable) {
		this.forkable = forkable;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getCloneUrl() {
		return cloneUrl;
	}

	public void setCloneUrl(String cloneUrl) {
		this.cloneUrl = cloneUrl;
	}

	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}

	public Links getLinks() {
		return links;
	}

	public void setLinks(Links links) {
		this.links = links;
	}

	@JsonProperty("public")
	public String getIsPublic() {
		return isPublic;
	}

	@JsonProperty("public")
	public void setIsPublic(String isPublic) {
		this.isPublic = isPublic;
	}
	
	 @Override
	  public String toString() {
	    return MoreObjects.toStringHelper(this.getClass()).add("id", id).add("project", project)
	        .add("scmId", scmId).add("slug", slug).add("forkable", forkable).add("link", link)
	        .add("name", name).add("state", state).add("cloneUrl", cloneUrl).add("links", links)
	        .toString();
	  }

}
