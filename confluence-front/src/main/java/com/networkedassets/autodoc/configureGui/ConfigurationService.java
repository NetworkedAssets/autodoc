package com.networkedassets.autodoc.configureGui;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.core.util.ClassLoaderUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.networkedassets.autodoc.transformer.Response;
import com.networkedassets.autodoc.transformer.TransformerServer;
import com.networkedassets.autodoc.transformer.settings.Project;
import com.networkedassets.autodoc.transformer.settings.Settings;
import com.networkedassets.autodoc.transformer.settings.SettingsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/configuration/")

@Produces({ MediaType.APPLICATION_JSON })
public class ConfigurationService {

	private static final Logger log = LoggerFactory.getLogger(ConfigurationService.class);
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private PageManager pageManager;
	private SpaceManager spaceManager;
	private TransformerServer transformerServer;

	public ConfigurationService(PageManager pageManager, SpaceManager spaceManager, SettingsManager settingsManager) {

		this.pageManager = pageManager;
		this.spaceManager = spaceManager;
		this.transformerServer = new TransformerServer(getTransformerUrl(),
				settingsManager.getGlobalSettings().getBaseUrl());
	}

	@Path("{space}/projects")
	@GET
	public String getProjects(@PathParam("space") String spaceKey) {

		Settings settings = new Settings();
		try {
			Response settingsForSpace = transformerServer.getSettings();
			settings = settingsForSpace.body;
		} catch (SettingsException e) {
			throw new TransformerSettingsException(String.format("{\"error\":\"%s\"}", e.getMessage()));
		}

		List<Project> projects = settings.getSources().stream().map(source -> source.projects)
				.flatMap(p -> p.values().stream()).collect(Collectors.toList());
		Optional<Long> defaultLocation = findDefaultLocation(spaceKey);
		defaultLocation.ifPresent(pageId -> projects.forEach(p -> p.setDefaultJavadocLocation(pageId)));

		return new Gson().toJson(settings);

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

	@Path("{space}/pages")
	@GET
	public String getConfluencePages(@PathParam("space") String spaceKey) {
		List<SimplePage> pages = getPages(spaceKey);
		return new Gson().toJson(pages);
	}

	private Optional<Long> findDefaultLocation(String key) {
		return Optional.ofNullable(pageManager.getPage(key, "5. Building Block View")).map(Page::getId);
	}

	private List<SimplePage> getPages(String key) {
		return pageManager.getPages(getSpace(key), true).stream().map(SimplePage::new).collect(Collectors.toList());
	}

	private Space getSpace(String key) {
		return spaceManager.getSpace(key);
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
