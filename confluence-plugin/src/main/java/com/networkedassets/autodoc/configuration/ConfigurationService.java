package com.networkedassets.autodoc.configuration;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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

import com.atlassian.applinks.api.*;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.ResponseException;
import com.google.common.base.Strings;
import com.networkedassets.autodoc.clients.atlassian.atlassianProjectsData.Project;
import com.networkedassets.autodoc.transformer.settings.*;
import com.networkedassets.autodoc.transformer.settings.Branch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.UserAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.networkedassets.autodoc.TransformerClient;
import com.networkedassets.autodoc.clients.atlassian.atlassianProjectsData.*;

@Path("/configuration/")
public class ConfigurationService {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationService.class);
    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final TransformerClient transformerClient;
    private final UserAccessor userAccesor;
    private final SettingsManager settingsManager;
    private final SourceManager sourceManager;
    private final ApplicationLinkService appLinkService;
    private final String ERROR_FORMAT = "{\"error\":\"%s\"}";
    private final DocSettingsService docSettingsService;

    public ConfigurationService(SettingsManager settingsManager, UserAccessor userAccessor,
                                ApplicationLinkService appLinkService, DocSettingsService docSettingsService) {

        this.userAccesor = userAccessor;
        this.settingsManager = settingsManager;
        this.appLinkService = appLinkService;
        this.docSettingsService = docSettingsService;
        this.transformerClient = new TransformerClient(getTransformerUrl());
        this.sourceManager = new SourceManager(appLinkService, transformerClient);

        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
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

            if (isProjectVisibleForCurrentUser(sourceId, projectKey)) {
                HttpResponse<String> response = transformerClient.modifyBranch(sourceId, projectKey, repoSlug, branchId,
                        branch);
                return convertToResponse(response);
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

        } catch (SettingsException | IOException | TypeNotInstalledException | CredentialsRequiredException | ResponseException e) {
            throw new TransformerSettingsException(String.format(ERROR_FORMAT, e.getMessage()));
        }
    }

    private boolean isProjectVisibleForCurrentUser(int sourceId, String projectKey)
            throws IOException, SettingsException, CredentialsRequiredException, TypeNotInstalledException, ResponseException {
        Source source = OBJECT_MAPPER.readValue(transformerClient.getSource(Integer.toString(sourceId)).getBody(),
                OBJECT_MAPPER.getTypeFactory().constructType(Source.class));
        ApplicationLinkRequest request = getApplicationLinkRequestFactory(source).createRequest(Request.MethodType.GET, "/rest/api/1.0/projects/" + projectKey);
        return request.executeAndReturn(com.atlassian.sal.api.net.Response::isSuccessful);
    }

    @POST
    @Path("credentials")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setCredentials(Credentials credentials) {

        try {
            if (userAccesor.authenticate(credentials.getConfluenceUsername(), credentials.getConfluencePassword())) {
                Settings settings = new Settings();
                settings.getCredentials().setConfluenceUsername(credentials.getConfluenceUsername());
                settings.getCredentials().setConfluencePassword(credentials.getConfluencePassword());
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

            Optional<List<String>> returnSources = Optional.of(sourceManager.updateSourcesFromAppLinks(currentSources));

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
    public Response getExtendedSourcesFilteredByCurrentUser() {
        try {
            List<Source> sources = OBJECT_MAPPER.readValue(transformerClient.getExtendedSources().getBody(),
                    OBJECT_MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, Source.class));

            List<Source> filteredSources = filterSourcesByCurrentUserPermissions(sources);

            return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON)
                    .entity(OBJECT_MAPPER.writeValueAsString(filteredSources)).build();


        } catch (IOException | SettingsException e) {
            throw new TransformerSettingsException(String.format(ERROR_FORMAT, e.getMessage()));
        }
    }

    private List<Source> filterSourcesByCurrentUserPermissions(List<Source> givenSources) {
        List<Source> sources = new ArrayList<>(givenSources);
        sources.stream().filter(s -> !Strings.isNullOrEmpty(s.getAppLinksId())).forEach(source -> {
            try {
                //get aviable project keys for current user from stash/bitbucket
                ApplicationLinkRequestFactory authenticatedRequestFactory = getApplicationLinkRequestFactory(source);
                String response = authenticatedRequestFactory
                        .createRequest(Request.MethodType.GET, "/rest/api/1.0/projects").execute();
                ProjectsPage projectsPage = OBJECT_MAPPER
                        .readValue(response, OBJECT_MAPPER.getTypeFactory().constructType(ProjectsPage.class));
                List<String> aviableProjectsKeys = projectsPage
                        .getProjects().stream().map(Project::getKey).collect(Collectors.toList());

                //filter given sources with obtained keys
                source.getProjects().values().removeIf(p -> !aviableProjectsKeys.contains(p.getKey()));
            } catch (TypeNotInstalledException | CredentialsRequiredException | IOException | ResponseException e) {
                throw new TransformerSettingsException(String.format(ERROR_FORMAT, e.getMessage()));
            }
        });
        return sources;
    }

    private ApplicationLinkRequestFactory getApplicationLinkRequestFactory(Source source) throws TypeNotInstalledException {
        return appLinkService
                .getApplicationLink(new ApplicationId(source.getAppLinksId()))
                .createAuthenticatedRequestFactory();
    }

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
        Response.ResponseBuilder rBuilder = Response.status(httpResponse.getStatus()).entity(httpResponse.getBody());
        httpResponse.getHeaders().forEach(rBuilder::header);
        return rBuilder.build();
    }

    private String getTransformerUrl() {
        return docSettingsService.getTransformerUrl();
    }

}
