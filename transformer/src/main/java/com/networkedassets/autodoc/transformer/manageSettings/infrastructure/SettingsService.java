package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;

import com.networkedassets.autodoc.transformer.manageSettings.provide.in.BranchModifier;
import com.networkedassets.autodoc.transformer.manageSettings.provide.in.EventScheduler;
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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Optional;

/**
 * REST service providing and receiving settings
 */

@Path("/settings")
public class SettingsService extends RestService {

	static final Logger log = LoggerFactory.getLogger(SettingsService.class);
	private SettingsProvider settingsProvider;
	private BranchModifier branchModifier;
	private SettingsSaver settingsSaver;
	private EventScheduler eventScheduler;

	@Inject
	public SettingsService(SettingsProvider settingsProvider, SettingsSaver settingsSaver,
			BranchModifier branchModifier) {
		this.settingsProvider = settingsProvider;
		this.branchModifier = branchModifier;
		this.settingsSaver = settingsSaver;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Settings getSettings() {
		log.info("GET request for settings handled");
		return settingsProvider.getCurrentSettings();
	}

	@Path("/confluence/credentials")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setConfluenceCredentials(Settings settings) {

		settingsSaver.setConfluenceCredentials(settings);
		return Response.status(Response.Status.ACCEPTED).build();
	}

	@POST
	@Path("/branches/{sourceId}/{projectKey}/{repoSlug}/{branchId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response modifyBranch(@PathParam("sourceId") int sourceId, @PathParam("projectKey") String projectKey,
			@PathParam("repoSlug") String repoSlug, @PathParam("branchId") String branchId, Branch branch) {
		try {
			projectKey = URLDecoder.decode(projectKey, "UTF-8");
			repoSlug = URLDecoder.decode(repoSlug, "UTF-8");
			branchId = URLDecoder.decode(branchId, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			log.error("Error while modifying branch: ", e);
			throw new SettingsServiceException(String.format("{\"error\":\"%s\"}", e.getMessage()));
		}

		Optional<Branch> modifiedBranch = Optional
				.ofNullable(branchModifier.modifyBranch(sourceId, projectKey, repoSlug, branchId, branch));

		eventScheduler.scheduleEvents(modifiedBranch.get(), sourceId, projectKey, repoSlug, branchId);
		return modifiedBranch
				.map(b -> Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(b).build())
				.orElseGet(() -> Response.status(Response.Status.BAD_REQUEST).entity("Wrong parameters").build());

	}
}
