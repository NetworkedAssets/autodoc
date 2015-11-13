package com.networkedassets.autodoc.transformer.delivery;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.networkedassets.autodoc.transformer.settings.SettingsForSpace;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.SettingsProvider;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.SettingsSetter;

/**
 * REST service providing and receiving settings
 */

@Path("/settings")
public class SettingsService extends RestService {

	static final Logger log = LoggerFactory.getLogger(SettingsService.class);
	private SettingsProvider settingsProvider;
	private SettingsSetter settingsSetter;

	@Inject
	public SettingsService(SettingsProvider settingsProvider, SettingsSetter settingsSetter) {
		this.settingsProvider = settingsProvider;
		this.settingsSetter = settingsSetter;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SettingsForSpace getSettingsForSpace(@QueryParam("spaceKey") String spaceKey,
			@QueryParam("confluenceUrl") String confluenceUrl) {
		log.info("GET request for settings handled");
		return settingsProvider.getSettingsForSpace(spaceKey, confluenceUrl);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public String setSettingsForSpace(SettingsForSpace settingsForSpace) {
		log.info("POST request for settings handled: " + settingsForSpace.toString());
		settingsSetter.setSettingsForSpace(settingsForSpace, settingsForSpace.getSpaceKey(),
				settingsForSpace.getConfluenceUrl());
		return SUCCESS;
	}

}
