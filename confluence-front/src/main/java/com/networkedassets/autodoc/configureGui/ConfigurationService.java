package com.networkedassets.autodoc.configureGui;

import com.atlassian.applinks.api.*;
import com.atlassian.applinks.api.application.stash.StashApplicationType;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.util.ClassLoaderUtils;
import com.atlassian.sal.api.net.Request.MethodType;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ReturningResponseHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mashape.unirest.http.HttpResponse;
import com.networkedassets.autodoc.configureGui.data.Credentials;
import com.networkedassets.autodoc.transformer.TransformerClient;
import com.networkedassets.autodoc.transformer.settings.Branch;
import com.networkedassets.autodoc.transformer.settings.Settings;
import com.networkedassets.autodoc.transformer.settings.SettingsException;
import com.networkedassets.autodoc.transformer.settings.Source;
import com.networkedassets.autodoc.transformer.settings.Source.SourceType;
import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

@Path("/configuration/")
public class ConfigurationService {

	private static final Logger log = LoggerFactory.getLogger(ConfigurationService.class);
	private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private final TransformerClient transformerClient;
	private final UserAccessor userAccesor;
	private final SettingsManager settingsManager;
	private final SourceManager sourceManager;
	private final String ERROR_FORMAT = "{\"error\":\"%s\"}";

	public ConfigurationService(SettingsManager settingsManager, UserAccessor userAccessor,
			final ApplicationLinkService appLinkService) {

		this.userAccesor = userAccessor;
		this.settingsManager = settingsManager;
		this.transformerClient = new TransformerClient(getTransformerUrl());
		this.sourceManager = new SourceManager(appLinkService, transformerClient);

	}

	@PUT
	@Path("sources/{sourceId}/{projectKey}/{repoSlug}/{branchId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response modifyBranch(@PathParam("sourceId") int sourceId, @PathParam("projectKey") String projectKey,
			@PathParam("repoSlug") String repoSlug, @PathParam("branchId") String branchId, Branch branch)
					throws JsonProcessingException {
		try {
			projectKey = URLDecoder.decode(projectKey, "UTF-8");
			repoSlug = URLDecoder.decode(repoSlug, "UTF-8");
			branchId = URLDecoder.decode(branchId, "UTF-8");
			HttpResponse<String> response = transformerClient.modifyBranch(sourceId, projectKey, repoSlug, branchId,
					branch);
			return convertToResponse(response);
		} catch (SettingsException | UnsupportedEncodingException e) {
			throw new TransformerSettingsException(String.format(ERROR_FORMAT, e.getMessage()));
		}
	}

	@POST
	@Path("credentials")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setCredentials(Credentials credentials) {

		try {
			if (userAccesor.authenticate(credentials.getConfluenceUsername(), credentials.getConfluencePassword())) {
				Settings settings = new Settings();
				settings.setConfluenceUsername(credentials.getConfluenceUsername());
				settings.setConfluencePassword(credentials.getConfluencePassword());
				settings.setConfluenceUrl(settingsManager.getGlobalSettings().getBaseUrl());
				HttpResponse<String> response = transformerClient.setCredentials(settings);
				return convertToResponse(response);
			} else
				return Response.status(Response.Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON)
						.entity(String.format(ERROR_FORMAT, "Incorrect username or password")).build();
		} catch (SettingsException e) {
			throw new TransformerSettingsException(String.format(ERROR_FORMAT, e.getMessage()));
		}
	}

	@GET
	@Path("credentials")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCredentials() {
		try {
			HttpResponse<String> response = transformerClient.getCredentials();
			return convertToResponse(response);
		} catch (SettingsException e) {
			throw new TransformerSettingsException(String.format(ERROR_FORMAT, e.getMessage()));
		}
	}

	@POST
	@Path("applinks/sources")
	public Response setSourcesFromAppLinks() {

		List<Source> currentSources;
		try {
			currentSources = OBJECT_MAPPER.readValue(transformerClient.getSources().getBody(),
					OBJECT_MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, Source.class));

			Optional<List<String>> returnSources = Optional.of(sourceManager.updateSourceFromAppLinks(currentSources));

			return returnSources.filter(s -> !s.isEmpty())
					.map(g -> Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON)
							.entity(String.format("{\"sources\": %s}", g)).build())
					.orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
		} catch (IOException | SettingsException e) {
			throw new TransformerSettingsException(String.format(ERROR_FORMAT, e.getMessage()));
		}
	}

	@Path("branches/listened")
	@GET
	public Response getListenedBranches() {
		try {
			List<Source> sources = OBJECT_MAPPER.readValue(transformerClient.getExtendedSources().getBody(),
					OBJECT_MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, Source.class));

			sources.forEach(s -> {
				s.getProjects().forEach((kp, p) -> {
					p.getRepos().forEach((kr, r) -> r.getBranches().values()
							.removeIf(b -> b.getListenTo() == Branch.ListenType.none));
					p.getRepos().values().removeIf(r -> r.getBranches().isEmpty());
				});
				s.getProjects().values().removeIf(p -> p.getRepos().isEmpty());
			});
			sources.removeIf(s -> s.getProjects().isEmpty());

			return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON)
					.entity(String.format("{\"sources\": %s}", OBJECT_MAPPER.writeValueAsString(sources))).build();
		} catch (SettingsException | IOException e) {
			throw new TransformerSettingsException(String.format(ERROR_FORMAT, e.getMessage()));
		}
	}

	@Path("event/{sourceUrl}/{projectKey}/{repoSlug}/{branchId}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response forceGenerate(@PathParam("sourceUrl") String sourceUrl, @PathParam("projectKey") String projectKey,
			@PathParam("repoSlug") String repoSlug, @PathParam("branchId") String branchId) {
		try {
			HttpResponse<String> response = transformerClient.forceGenerate(URLDecoder.decode(sourceUrl, "UTF-8"),
					URLDecoder.decode(projectKey, "UTF-8"), URLDecoder.decode(repoSlug, "UTF-8"),
					URLDecoder.decode(branchId, "UTF-8"));
			return convertToResponse(response);
		} catch (SettingsException | UnsupportedEncodingException e) {
			throw new TransformerSettingsException(String.format(ERROR_FORMAT, e.getMessage()));
		}
	}

	@Path("sources")
	@GET
	public Response getSources() {
		try {
			HttpResponse<String> response = transformerClient.getSources();
			return convertToResponse(response);
		} catch (SettingsException e) {
			throw new TransformerSettingsException(String.format(ERROR_FORMAT, e.getMessage()));
		}
	}

	@Path("sources/extended")
	@GET
	public Response getExtendedSources() {
		try {
			HttpResponse<String> response = transformerClient.getExtendedSources();
			return convertToResponse(response);
		} catch (SettingsException e) {
			throw new TransformerSettingsException(String.format(ERROR_FORMAT, e.getMessage()));
		}

	}

	@Path("sources/{id}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeSource(@PathParam("id") String sourceId) {
		try {
			HttpResponse<String> response = transformerClient.removeSource(sourceId);
			return convertToResponse(response);
		} catch (SettingsException e) {
			throw new TransformerSettingsException(String.format(ERROR_FORMAT, e.getMessage()));
		}
	}

	@Path("sources")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setSource(Source source) {
		try {
			HttpResponse<String> response = transformerClient.setSource(source);
			return convertToResponse(response);
		} catch (SettingsException e) {
			throw new TransformerSettingsException(String.format(ERROR_FORMAT, e.getMessage()));
		}
	}

	@Path("sources/{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setSource(@PathParam("id") String sourceId, Source source) {
		try {
			HttpResponse<String> response = transformerClient.changeSource(sourceId, source);
			return convertToResponse(response);
		} catch (SettingsException e) {
			throw new TransformerSettingsException(String.format(ERROR_FORMAT, e.getMessage()));
		}
	}

	private Response convertToResponse(HttpResponse<String> httpResponse) {
		Response.ResponseBuilder rBuilder = Response.status(httpResponse.getStatus()).type(MediaType.APPLICATION_JSON)
				.entity(httpResponse.getBody());
		httpResponse.getHeaders().forEach((s, strings) -> rBuilder.header(s, strings));
		return rBuilder.build();
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
