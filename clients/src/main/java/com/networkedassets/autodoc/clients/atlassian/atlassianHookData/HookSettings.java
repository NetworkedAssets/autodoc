package com.networkedassets.autodoc.clients.atlassian.atlassianHookData;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HookSettings {

	@Nonnull
	private String url;
	@Nonnull
	private String timeout;

	@Nonnull
	public String getUrl() {
		return url;
	}

	@Nonnull
	public void setUrl(String url) {
		this.url = url;
	}

	@Nonnull
	public String getTimeout() {
		return timeout;
	}

	@Nonnull
	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

}
