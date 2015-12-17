package com.networkedassets.autodoc.configureGui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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

import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.core.util.ClassLoaderUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.networkedassets.autodoc.transformer.TransformerServer;
import com.networkedassets.autodoc.transformer.settings.Settings;
import com.networkedassets.autodoc.transformer.settings.SettingsException;

@Path("/configuration/")
public class ConfigurationService {

	private static final Logger log = LoggerFactory.getLogger(ConfigurationService.class);
	private static ObjectMapper objectMapper = new ObjectMapper();
	private TransformerServer transformerServer;

	public ConfigurationService(SettingsManager settingsManager) {

		this.transformerServer = new TransformerServer(getTransformerUrl(),
				settingsManager.getGlobalSettings().getBaseUrl());
	}

	@Path("{space}/projects")
	@GET
	public Response getProjects(@PathParam("space") String spaceKey) {

		Settings settings = null;
		try {
			com.networkedassets.autodoc.transformer.Response settingsForSpace = transformerServer.getSettings();
			settings = settingsForSpace.body;
			return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON)
					.entity(objectMapper.writeValueAsString(settings)).build();
		} catch (SettingsException | JsonProcessingException e) {
			throw new TransformerSettingsException(String.format("{\"error\":\"%s\"}", e.getMessage()));
		}

	}

	@Path("{space}/projects")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public String setProjects(@PathParam("space") String spaceKey, Settings settings) {

		HttpResponse<String> response = null;
		try {
			response = transformerServer.saveSettingsForSpace(settings);
		} catch (SettingsException e) {
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
