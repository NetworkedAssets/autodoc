package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;

import com.networkedassets.autodoc.transformer.manageSettings.provide.in.SettingsSaver;
import com.networkedassets.autodoc.transformer.manageSettings.provide.out.SettingsProvider;
import com.networkedassets.autodoc.transformer.settings.SettingsForSpace;
import com.networkedassets.autodoc.transformer.util.RestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Objects;

/**
 * REST service providing and receiving settings
 */

@Path("/settings")
public class SettingsService extends RestService {


	static final Logger log = LoggerFactory.getLogger(SettingsService.class);
	private SettingsProvider settingsProvider;
	private SettingsSaver settingsSetter;

	@Inject
	public SettingsService(SettingsProvider settingsProvider, SettingsSaver settingsSetter) {
		this.settingsProvider = settingsProvider;
		this.settingsSetter = settingsSetter;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SettingsForSpace getSettingsForSpace(@QueryParam("spaceKey") String spaceKey,
			@QueryParam("confluenceUrl") String confluenceUrl) {
		log.info("GET request for settings handled");
        if(Objects.isNull(spaceKey) || Objects.isNull(confluenceUrl)){
            log.error("Wrong parameters");

            return null;
        }
		return settingsProvider.getSettingsForSpace(spaceKey, confluenceUrl);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public SettingsForSpace setSettingsForSpace(SettingsForSpace settingsForSpace) {
		log.info("POST request for settings handled: " + settingsForSpace.toString());
		settingsSetter.setSettingsForSpace(settingsForSpace, settingsForSpace.getSpaceKey(),
				settingsForSpace.getConfluenceUrl());
		return settingsProvider.getSettingsForSpace(settingsForSpace.getSpaceKey(), settingsForSpace.getConfluenceUrl());
	}



}
