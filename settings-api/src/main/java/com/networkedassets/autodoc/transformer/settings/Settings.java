package com.networkedassets.autodoc.transformer.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains settings of the application
 */
public class Settings implements Serializable {

	private static final long serialVersionUID = 1L;
	private String confluenceUrl = "http://46.101.240.138:8090/";
	private String confluenceUsername = "mrobakowski";
	private String confluencePassword = "admin";
	private TransformerSettings transformerSettings = new TransformerSettings();
	private List<Source> sources = new ArrayList<>();

	public Source getSourceByUrl(String url) {
		return sources.stream().filter(source -> source.getUrl().equals(url)).findAny().orElse(null);
	}

	public Source getSourceById(int id) {
		return sources.stream().filter(source -> source.getId() == id).findAny().orElse(null);
	}

	public boolean isSourceWithUrlExistent(String url) {
		return sources.stream().anyMatch(source -> source.getUrl().equals(url));
	}

	@JsonProperty("transformerSettings")
	public TransformerSettings getTransformerSettings() {
		return transformerSettings;
	}

	public void setTransformerSettings(TransformerSettings transformerSettings) {
		this.transformerSettings = transformerSettings;
	}

	@JsonProperty("sources")
	public List<Source> getSources() {
		return sources;
	}

	public void setSources(List<Source> sources) {
		this.sources = sources;
	}

	@JsonProperty("confluenceUrl")
	public String getConfluenceUrl() {
		return confluenceUrl;
	}

	public void setConfluenceUrl(String confluenceUrl) {
		this.confluenceUrl = confluenceUrl;
	}

	@JsonProperty("confluenceUsername")
	public String getConfluenceUsername() {
		return confluenceUsername;
	}

	public void setConfluenceUsername(String confluenceUsername) {
		this.confluenceUsername = confluenceUsername;
	}

	@JsonProperty("confluencePassword")
	public String getConfluencePassword() {
		return confluencePassword;
	}

	public void setConfluencePassword(String confluencePassword) {
		this.confluencePassword = confluencePassword;
	}
}
