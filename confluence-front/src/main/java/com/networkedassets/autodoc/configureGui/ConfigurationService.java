package com.networkedassets.autodoc.configureGui;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.application.stash.StashApplicationType;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.util.ClassLoaderUtils;
import com.atlassian.sal.api.net.Request.MethodType;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ReturningResponseHandler;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.networkedassets.autodoc.configureGui.data.Credentials;
import com.networkedassets.autodoc.transformer.TransformerServer;
import com.networkedassets.autodoc.transformer.settings.Branch;
import com.networkedassets.autodoc.transformer.settings.Settings;
import com.networkedassets.autodoc.transformer.settings.SettingsException;
import com.networkedassets.autodoc.transformer.settings.Source;
import com.networkedassets.autodoc.transformer.settings.Source.SourceType;
import com.networkedassets.autodoc.transformer.settings.view.Views;

@Path("/configuration/")
public class ConfigurationService {

	private static final Logger log = LoggerFactory.getLogger(ConfigurationService.class);
	private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private TransformerServer transformerServer;
	private final ApplicationLinkService appLinkService;
	private final UserAccessor userAccesor;
	private final SettingsManager settingsManager;

	public ConfigurationService(SettingsManager settingsManager, UserAccessor userAccessor,
			final ApplicationLinkService appLinkService) {

		this.appLinkService = appLinkService;
		this.userAccesor = userAccessor;
		this.settingsManager = settingsManager;
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

	@POST
	@Path("/credentials")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setCredentials(Credentials credentials) {

		try {

			if (userAccesor.authenticate(credentials.getConfluenceUsername(), credentials.getConfluencePassword())) {
				Settings settings = new Settings();
				settings.setConfluenceUsername(credentials.getConfluenceUsername());
				settings.setConfluencePassword(credentials.getConfluencePassword());
				settings.setConfluenceUrl(settingsManager.getGlobalSettings().getBaseUrl());
				HttpResponse<String> response = transformerServer.setCredentials(settings);
				return Response.status(response.getStatus()).type(MediaType.APPLICATION_JSON).build();
			} else
				return Response.status(Response.Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON)
						.entity(String.format("{\"error\":\"%s\"}", "Incorrect username or password")).build();

		} catch (SettingsException e) {
			throw new TransformerSettingsException(String.format("{\"error\":\"%s\"}", e.getMessage()));
		}

	}

	@POST
	@Path("applinks/sources")
	public Response setSourcesFromAppLinks() {

		List<String> sources = new ArrayList<String>();

		appLinkService.getApplicationLinks(StashApplicationType.class).forEach(appLinks -> {

			Optional<SourceType> sourceType = getAppLinkSourceType(appLinks);
			if (sourceType.isPresent()) {

				Source source = new Source();
				source.setName(appLinks.getName());
				source.setUrl(appLinks.getRpcUrl().toString());
				source.setSourceType(sourceType.get());

				try {
					HttpResponse<Source> response = transformerServer.setSource(source);
					sources.add(OBJECT_MAPPER.writeValueAsString(response.getBody()));
				} catch (Exception e) {
					throw new TransformerSettingsException(String.format("{\"error\":\"%s\"}", e.getMessage()));
				}
			}

		});

		Optional<List<String>> returnSources = Optional.of(sources);

		return returnSources.filter(s -> !s.isEmpty())
				.map(g -> Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON)
						.entity(String.format("{\"sources\": %s}", g)).build())
				.orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());

	}

	@Path("branches/listened")
	@GET
	public Response getListenedBranches() {
		try {
			List<Source> sources = transformerServer.getSettings().getSources();

			sources.forEach(s -> {
				s.projects.forEach((kp, p) -> {
					p.repos.forEach(
							(kr, r) -> r.branches.values().removeIf(b -> b.getListenTo() == Branch.ListenType.none));
					p.repos.values().removeIf(r -> r.branches.isEmpty());
				});
				s.projects.values().removeIf(p -> p.repos.isEmpty());
			});
			sources.removeIf(s -> s.projects.isEmpty());

			return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON)
					.entity(String.format("{\"sources\": %s}", OBJECT_MAPPER.writeValueAsString(sources))).build();
		} catch (SettingsException | JsonProcessingException e) {
			throw new TransformerSettingsException(String.format("{\"error\":\"%s\"}", e.getMessage()));
		}

	}

	@Path("branches/{sourceId}/{projectKey}/{repoSlug}/{branchId}")
	@POST
	public Response setBranches(@PathParam("sourceId") int sourceId, @PathParam("projectKey") String projectKey,
			@PathParam("repoSlug") String repoSlug, @PathParam("branchId") String branchId, Branch branch) {
		Branch modifiedBranch = null;
		try {
			projectKey = URLDecoder.decode(projectKey, "UTF-8");
			repoSlug = URLDecoder.decode(repoSlug, "UTF-8");
			branchId = URLDecoder.decode(branchId, "UTF-8");
			modifiedBranch = transformerServer.modifyBranch(sourceId, projectKey, repoSlug, branch);
		} catch (SettingsException e) {
			throw new TransformerSettingsException(String.format("{\"error\":\"%s\"}", e.getMessage()));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return Response.status(Response.Status.OK).entity(modifiedBranch).build();
	}

	@Path("event/{sourceUrl}/{projectKey}/{repoSlug}/{branchId}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String setForceGenerate(@PathParam("sourceUrl") String sourceUrl, @PathParam("projectKey") String projectKey,
			@PathParam("repoSlug") String repoSlug, @PathParam("branchId") String branchId) {

		HttpResponse<String> response;
		try {
			response = transformerServer.forceRegenerate(URLDecoder.decode(sourceUrl, "UTF-8"),
					URLDecoder.decode(projectKey, "UTF-8"), URLDecoder.decode(repoSlug, "UTF-8"),
					URLDecoder.decode(branchId, "UTF-8"));
		} catch (SettingsException | UnsupportedEncodingException e) {
			throw new TransformerSettingsException(String.format("{\"error\":\"%s\"}", e.getMessage()));
		}
		return response.getBody();

	}

	@Path("source/{id}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeSource(@PathParam("id") int sourceId) {
		try {
			HttpResponse<String> response = transformerServer.removeSource(sourceId);
			return Response.status(response.getStatus()).type(MediaType.APPLICATION_JSON).build();
		} catch (SettingsException e) {
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

	@Path("sources")
	@GET
	public Response getSources() {

		try {
			HttpResponse<String> response = transformerServer.getSources();
			return Response.status(response.getStatus()).type(MediaType.APPLICATION_JSON)
					.entity(String.format("{\"sources\": %s}", response.getBody())).build();
		} catch (SettingsException e) {
			throw new TransformerSettingsException(String.format("{\"error\":\"%s\"}", e.getMessage()));
		}

	}

	@Path("source/{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setSource(@PathParam("id") int sourceId, Source source) {
		try {
			HttpResponse<Source> response = transformerServer.changeSource(sourceId, source);
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

	private Optional<SourceType> getAppLinkSourceType(ApplicationLink appLink) {
		String requestUrl = "/rest/api/1.0/application-properties";
		Optional<SourceType> sourceType = Optional.empty();

		ApplicationLinkRequestFactory requestFactory = appLink.createAuthenticatedRequestFactory();

		try {
			ApplicationLinkRequest request = requestFactory.createRequest(MethodType.GET, requestUrl);
			sourceType = request.executeAndReturn(
					new ReturningResponseHandler<com.atlassian.sal.api.net.Response, Optional<SourceType>>() {
						@Override
						public Optional<SourceType> handle(com.atlassian.sal.api.net.Response response)
								throws ResponseException {
							if (response.isSuccessful() || response.getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
								return Optional.of(response.getResponseBodyAsString().contains("Stash")
										? SourceType.STASH : SourceType.BITBUCKET);
							}
							throw new ResponseException(
									String.format("Execute applink with error! [statusCode=%s, statusText=%s]",
											response.getStatusCode(), response.getStatusText()));
						}
					});

		} catch (CredentialsRequiredException | ResponseException e) {
			log.error("Couldn't get appLinks", e);
		}

		return sourceType;

	}

}
