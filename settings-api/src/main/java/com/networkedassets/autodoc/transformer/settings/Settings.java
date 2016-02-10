package com.networkedassets.autodoc.transformer.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.Optional;
import com.networkedassets.autodoc.transformer.settings.view.Views;

/**
 * Contains settings of the application
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Settings implements Serializable {

	private static final long serialVersionUID = 3847560203140549969L;
	@JsonView(Views.GetSettingsView.class)
	private String confluenceUrl;
	@JsonView(Views.GetSettingsView.class)
	private TransformerSettings transformerSettings = new TransformerSettings();
	@JsonView(Views.GetSourcesView.class)
	private List<Source> sources = new ArrayList<>();
	@JsonView(Views.GetCredentialsView.class)
	private Credentials credentials = new Credentials();

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
		return credentials.getConfluenceUsername();
	}

	public void setConfluenceUsername(String confluenceUsername) {
		this.credentials.setConfluenceUsername(confluenceUsername);
	}

	@JsonProperty("confluencePassword")
	public String getConfluencePassword() {
		return credentials.getConfluencePassword();
	}

	public void setConfluencePassword(String confluencePassword) {
		credentials.setConfluencePassword(confluencePassword);
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}
}
