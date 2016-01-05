package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;

import com.networkedassets.autodoc.transformer.manageSettings.provide.in.SettingsSaver;
import com.networkedassets.autodoc.transformer.manageSettings.provide.out.SettingsProvider;
import com.networkedassets.autodoc.transformer.settings.Branch;
import com.networkedassets.autodoc.transformer.settings.Settings;
import com.networkedassets.autodoc.transformer.util.RestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
	public Settings getSettings() {
		log.info("GET request for settings handled");
		return settingsProvider.getCurrentSettings();
	}

	// TODO: implement
	@Path("/branches/{sourceId}/{projectKey}/{repoSlug}/{branchId}")
	@POST
	public Response setBranches(@PathParam("sourceId") int sourceId, @PathParam("projectKey") String projectKey,
			@PathParam("repoSlug") String repoSlug, @PathParam("branchId") String branchId, Branch branch) {

		return null;
	}

	// TODO: check if necessary
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Settings setSettingsForSpace(Settings settings) {
		log.info("POST request for settings handled: " + settings.toString());
		settingsSetter.setCurrentSettings(settings);
		return settingsProvider.getCurrentSettings();
	}

}
