package com.networkedassets.autodoc.transformer.clients.atlassian.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public class Project {

	private String key;
	private Integer id;
	private String name;
	private String description;
	@JsonProperty("public")
	private Boolean isPublic;
	private String type;
	private Link link;
	private Links links;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
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

	public String getDescription() {
		return description;
	}

	@JsonProperty("public")
	public Boolean getIsPublic() {
		return isPublic;
	}

	@JsonProperty("public")
	public void setIsPublic(Boolean isPublic) {
		this.isPublic = isPublic;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
	
	 @Override
	  public String toString() {
	    return MoreObjects.toStringHelper(this.getClass())
	        .add("key", key)
	        .add("id", id)
	        .add("name", name)
	        .add("description", description)
	        .add("isPublic", isPublic)
	        .add("type", type)
	        .add("link", link)
	        .add("links", links)
	        .toString();
	  }

}
