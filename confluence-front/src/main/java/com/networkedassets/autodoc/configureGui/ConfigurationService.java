package com.networkedassets.autodoc.configureGui;

import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.core.util.ClassLoaderUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.networkedassets.autodoc.transformer.TransformerServer;
import com.networkedassets.autodoc.transformer.settings.Branch;
import com.networkedassets.autodoc.transformer.settings.Settings;
import com.networkedassets.autodoc.transformer.settings.SettingsException;
import com.networkedassets.autodoc.transformer.settings.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Properties;

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

	@Path("branches/listened")
	@GET
	public Response getListenedBranches() {
		try {
			List<Source> sources = transformerServer.getSettings().getSources();

			sources.forEach(s -> {
				s.projects.forEach((kp, p) -> {
					p.repos.forEach((kr, r) ->
							r.branches.values().removeIf(b -> b.getListenTo() == Branch.ListenType.none));
					p.repos.values().removeIf(r -> r.branches.isEmpty());
				});
				s.projects.values().removeIf(p -> p.repos.isEmpty());
			});
			sources.removeIf(s -> s.projects.isEmpty());

			return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON)
					.entity(String.format("{\"sources\":\"%s\"}", OBJECT_MAPPER.writeValueAsString(sources))).build();
		} catch (SettingsException | JsonProcessingException e) {
			throw new TransformerSettingsException(String.format("{\"error\":\"%s\"}", e.getMessage()));
		}

	}

	@Path("branches/{sourceId}/{projectKey}/{repoSlug}/{branchId}")
	@POST
	public Response setBranches(@PathParam("sourceId") int sourceId, @PathParam("projectKey") String projectKey,
			@PathParam("repoSlug") String repoSlug, @PathParam("branchId") String branchId, Branch branch) {
        Branch modifiedBranch;
        try {
             modifiedBranch = transformerServer.modifyBranch(sourceId, projectKey, repoSlug, branch);
        } catch (SettingsException e) {
            throw new TransformerSettingsException(String.format("{\"error\":\"%s\"}", e.getMessage()));
        }

        return Response.status(Response.Status.OK).entity(modifiedBranch).build();
	}

	// TODO: check if necessary
	@Path("projects")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
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

}
