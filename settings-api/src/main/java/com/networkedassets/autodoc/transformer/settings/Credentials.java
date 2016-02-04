package com.networkedassets.autodoc.transformer.settings;

import com.fasterxml.jackson.annotation.JsonView;
import com.networkedassets.autodoc.transformer.settings.view.Views;

public class Credentials {

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
