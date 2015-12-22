package com.networkedassets.autodoc.configureGui;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
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
import com.networkedassets.autodoc.transformer.settings.Source;

@Path("/configuration/")
public class ConfigurationService {

	private static final Logger log = LoggerFactory.getLogger(ConfigurationService.class);
	private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
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
					.entity(OBJECT_MAPPER.writeValueAsString(settings)).build();
		} catch (SettingsException | JsonProcessingException e) {
			throw new TransformerSettingsException(String.format("{\"error\":\"%s\"}", e.getMessage()));
		}

	}

	@Path("projects/listened")
	@GET
	public Response getListenedProjects() {
		try {
			List<Source> sources = transformerServer.getSettings().getSources();

			// TODO: only branch isListened

			return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON)
					.entity(String.format("{\"sources\":\"%s\"}", OBJECT_MAPPER.writeValueAsString(sources))).build();
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
	@Produces(MediaType.APPLICATION_JSON)
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

	@Path("source/{id}")
	@GET
	public Response getSource(@PathParam("id") String id) {
		try {
			Source source = transformerServer.getSource(id);
			return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON)
					.entity(OBJECT_MAPPER.writeValueAsString(source)).build();
		} catch (SettingsException | JsonProcessingException e) {
			throw new TransformerSettingsException(String.format("{\"error\":\"%s\"}", e.getMessage()));
		}

	}

	@Path("source")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setSource(Source source) {
		try {
			HttpResponse<Source> response = transformerServer.setSource(source);
			return Response.status(response.getStatus()).type(MediaType.APPLICATION_JSON)
					.entity(OBJECT_MAPPER.writeValueAsString(response.getBody())).build();
		} catch (SettingsException | JsonProcessingException e) {
			throw new TransformerSettingsException(String.format("{\"error\":\"%s\"}", e.getMessage()));
		}

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
