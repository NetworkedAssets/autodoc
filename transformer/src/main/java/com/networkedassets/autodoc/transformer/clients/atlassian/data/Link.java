package com.networkedassets.autodoc.transformer.clients.atlassian.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Link {

	@JsonProperty("url")
	private String url;
	@JsonProperty("rel")
	private String rel;

	@JsonProperty("url")
	public String getUrl() {
		return url;
	}

	@JsonProperty("url")
	public void setUrl(String url) {
		this.url = url;
	}

	@JsonProperty("rel")
	public String getRel() {
		return rel;
	}

	@JsonProperty("rel")
	public void setRel(String rel) {
		this.rel = rel;
	}

}
