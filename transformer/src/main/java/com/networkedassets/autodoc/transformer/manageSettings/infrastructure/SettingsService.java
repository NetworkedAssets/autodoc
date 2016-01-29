package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;
import com.networkedassets.autodoc.transformer.manageSettings.provide.in.BranchModifier;
import com.networkedassets.autodoc.transformer.manageSettings.provide.in.EventScheduler;
import com.networkedassets.autodoc.transformer.manageSettings.provide.in.SettingsSaver;
import com.networkedassets.autodoc.transformer.manageSettings.provide.out.SettingsProvider;
import com.networkedassets.autodoc.transformer.settings.Branch;
import com.networkedassets.autodoc.transformer.settings.Settings;
import com.networkedassets.autodoc.transformer.settings.view.Views;
import com.networkedassets.autodoc.transformer.util.RestService;

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
			BranchModifier branchModifier, EventScheduler eventScheduler) {
		this.settingsProvider = settingsProvider;
		this.branchModifier = branchModifier;
		this.settingsSaver = settingsSaver;
		this.eventScheduler = eventScheduler;
	}

	@JsonView(Views.PublicView.class)
	@GET
	public Response getSettings() {
		log.info("GET request for settings handled");

		return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON)
				.entity(settingsProvider.getCurrentSettings()).build();

	}

	@Path("/credentials")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@JsonView(Views.PublicView.class)
	public Response setCredentials(Settings settings) {
		settingsSaver.setCredentials(settings);
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
