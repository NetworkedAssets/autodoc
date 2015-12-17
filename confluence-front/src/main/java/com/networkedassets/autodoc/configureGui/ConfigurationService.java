package com.networkedassets.autodoc.configureGui;

import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.core.util.ClassLoaderUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.networkedassets.autodoc.transformer.TransformerServer;
import com.networkedassets.autodoc.transformer.settings.Settings;
import com.networkedassets.autodoc.transformer.settings.SettingsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;

@Path("/configuration/")
public class ConfigurationService {

	private static final Logger log = LoggerFactory.getLogger(ConfigurationService.class);
	private static ObjectMapper objectMapper = new ObjectMapper();
	private TransformerServer transformerServer;

	public ConfigurationService(SettingsManager settingsManager) {

		this.transformerServer = new TransformerServer(getTransformerUrl(),
				settingsManager.getGlobalSettings().getBaseUrl());
	}

	@Path("projects")
	@GET
	public Response getProjects() {
		try {
			Settings settings = transformerServer.getSettings();
			return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON)
					.entity(objectMapper.writeValueAsString(settings)).build();
		} catch (SettingsException | JsonProcessingException e) {
			throw new TransformerSettingsException(String.format("{\"error\":\"%s\"}", e.getMessage()));
		}

	}

	@Path("projects")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public String setProjects(Settings settings) {

		HttpResponse<String> response;
		try {
			response = transformerServer.saveSettingsForSpace(settings);
		} catch (SettingsException e) {
			throw new TransformerSettingsException(String.format("{\"error\":\"%s\"}", e.getMessage()));
		}
		return response.getBody();

	}

	@Path("event/{projectKey}/{repoSlug}/{branchId}")
	@POST
	public String setForceGenerate(@PathParam("projectKey") String projectKey, @PathParam("repoSlug") String repoSlug,
								   @PathParam("branchId") String branchId) {

		HttpResponse<String> response;
		try {
			response = transformerServer.forceRegenerate(URLDecoder.decode(projectKey, "UTF-8"),
					URLDecoder.decode(repoSlug, "UTF-8"), URLDecoder.decode(branchId, "UTF-8"));
		} catch (SettingsException | UnsupportedEncodingException e) {
			throw new TransformerSettingsException(String.format("{\"error\":\"%s\"}", e.getMessage()));
		}
		return response.getBody();

	}

	private String getTransformerUrl() {
		InputStream properties = ClassLoaderUtils.getResourceAsStream("autodoc_confluence.properties", getClass());
		Properties props = new Properties();
		try {
			props.load(properties);
		} catch (IOException e) {
			log.error("Couldn't load the configuration file", e);
		}
		return props.getProperty("transformerUrl", "https://localhost:8050/");
	}

}
