package com.networkedassets.autodoc.transformer.settings;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.networkedassets.autodoc.transformer.settings.view.Views;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Credentials implements Serializable {
	
	private static final long serialVersionUID = 6921485233807433055L;
	
	@JsonView(Views.GetCredentialsView.class)
	private String confluenceUsername;
	@JsonView(Views.SetCredentialsView.class)
	private String confluencePassword;

	public String getConfluenceUsername() {
		return confluenceUsername;
	}

	public void setConfluenceUsername(String confluenceUsername) {
		this.confluenceUsername = confluenceUsername;
	}

	public String getConfluencePassword() {
		return confluencePassword;
	}

	public void setConfluencePassword(String confluencePassword) {
		this.confluencePassword = confluencePassword;
	}

}
