package com.networkedassets.autodoc.configureGui;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.networkedassets.autodoc.configureGui.data.Credentials;
import com.networkedassets.autodoc.transformer.TransformerClient;
import com.networkedassets.autodoc.transformer.settings.Branch;
import com.networkedassets.autodoc.transformer.settings.Settings;
import com.networkedassets.autodoc.transformer.settings.SettingsException;
import com.networkedassets.autodoc.transformer.settings.Source;
import com.networkedassets.autodoc.transformer.settings.Source.SourceType;

@Path("/configuration/")
public class ConfigurationService {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationService.class);
    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private TransformerClient transformerClient;
    private final ApplicationLinkService appLinkService;
    private final UserAccessor userAccesor;
    private final SettingsManager settingsManager;

    public ConfigurationService(SettingsManager settingsManager, UserAccessor userAccessor,
                                final ApplicationLinkService appLinkService) {

        this.appLinkService = appLinkService;
        this.userAccesor = userAccessor;
        this.settingsManager = settingsManager;
        this.transformerClient = new TransformerClient(getTransformerUrl());

    }

    @PUT
    @Path("sources/{sourceId}/{projectKey}/{repoSlug}/{branchId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modifyBranch(@PathParam("sourceId") int sourceId, @PathParam("projectKey") String projectKey,
                                 @PathParam("repoSlug") String repoSlug, @PathParam("branchId") String branchId, Branch branch) {
        System.out.println("!!!!!!!!!!!!!!!!!!!!HELLLOOOOOOOOOOOOOO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        Branch modifiedBranch = null;
        try {
            projectKey = URLDecoder.decode(projectKey, "UTF-8");
            repoSlug = URLDecoder.decode(repoSlug, "UTF-8");
            branchId = URLDecoder.decode(branchId, "UTF-8");
            modifiedBranch = transformerClient.modifyBranch(sourceId, projectKey, repoSlug, branchId, branch);
        } catch (SettingsException e) {
            throw new TransformerSettingsException(String.format("{\"error\":\"%s\"}", e.getMessage()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return Response.status(Response.Status.OK).entity(modifiedBranch).build();
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
                return Response.status(response.getStatus()).type(MediaType.APPLICATION_JSON).build();
            } else
                return Response.status(Response.Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON)
                        .entity(String.format("{\"error\":\"%s\"}", "Incorrect username or password")).build();

        } catch (SettingsException e) {
            throw new TransformerSettingsException(String.format("{\"error\":\"%s\"}", e.getMessage()));
        }
    }

    @GET
    @Path("credentials")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCredentials() {
        try {
            HttpResponse<String> response = transformerClient.getCredentials();
            return Response.status(response.getStatus()).type(MediaType.APPLICATION_JSON).entity(response.getBody())
                    .build();
        } catch (SettingsException e) {
            throw new TransformerSettingsException(String.format("{\"error\":\"%s\"}", e.getMessage()));
        }
    }

    //TODO: PUT for update by Admin and get source Id by appLinksID an use ready rest  put "source/{id}")

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
                source.setAppLinksId(appLinks.getId().toString());

                try {
                    HttpResponse<Source> response = transformerClient.setSource(source);
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
            List<Source> sources = Arrays
                    .asList(OBJECT_MAPPER.readValue(transformerClient.getSources().getBody(), Source[].class));

            sources.forEach(s -> {
                s.getProjects().forEach((kp, p) -> {
                    p.getRepos().forEach(
                            (kr, r) -> r.getBranches().values().removeIf(b -> b.getListenTo() == Branch.ListenType.none));
                    p.getRepos().values().removeIf(r -> r.getBranches().isEmpty());
                });
                s.getProjects().values().removeIf(p -> p.getRepos().isEmpty());
            });
            sources.removeIf(s -> s.getProjects().isEmpty());

            return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON)
                    .entity(String.format("{\"sources\": %s}", OBJECT_MAPPER.writeValueAsString(sources))).build();
        } catch (SettingsException | IOException e) {
            throw new TransformerSettingsException(String.format("{\"error\":\"%s\"}", e.getMessage()));
        }

    }

    @Path("event/{sourceUrl}/{projectKey}/{repoSlug}/{branchId}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String forceGenerate(@PathParam("sourceUrl") String sourceUrl, @PathParam("projectKey") String projectKey,
                                @PathParam("repoSlug") String repoSlug, @PathParam("branchId") String branchId) {

        HttpResponse<String> response;
        try {
            response = transformerClient.forceGenerate(URLDecoder.decode(sourceUrl, "UTF-8"),
                    URLDecoder.decode(projectKey, "UTF-8"), URLDecoder.decode(repoSlug, "UTF-8"),
                    URLDecoder.decode(branchId, "UTF-8"));
        } catch (SettingsException | UnsupportedEncodingException e) {
            throw new TransformerSettingsException(String.format("{\"error\":\"%s\"}", e.getMessage()));
        }
        return response.getBody();

    }

    @Path("sources")
    @GET
    public Response getSources() {

        try {
            HttpResponse<String> response = transformerClient.getSources();
            return Response.status(response.getStatus()).type(MediaType.APPLICATION_JSON).entity(response.getBody())
                    .build();
        } catch (SettingsException e) {
            throw new TransformerSettingsException(String.format("{\"error\":\"%s\"}", e.getMessage()));
        }

    }

    @Path("sources/extended")
    @GET
    public Response getExtendedSources() {

        try {
            HttpResponse<String> response = transformerClient.getExtendedSources();
            return Response.status(response.getStatus()).type(MediaType.APPLICATION_JSON)
                    .entity(String.format("{\"sources\": %s}", response.getBody())).build();
        } catch (SettingsException e) {
            throw new TransformerSettingsException(String.format("{\"error\":\"%s\"}", e.getMessage()));
        }

    }

    @Path("sources/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeSource(@PathParam("id") int sourceId) {
        try {
            HttpResponse<String> response = transformerClient.removeSource(sourceId);
            return Response.status(response.getStatus()).type(MediaType.APPLICATION_JSON).build();
        } catch (SettingsException e) {
            throw new TransformerSettingsException(String.format("{\"error\":\"%s\"}", e.getMessage()));
        }
    }

    @Path("sources")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setSource(Source source) {
        try {
            HttpResponse<Source> response = transformerClient.setSource(source);
            return Response.status(response.getStatus()).type(MediaType.APPLICATION_JSON)
                    .entity(OBJECT_MAPPER.writeValueAsString(response.getBody())).build();
        } catch (SettingsException | JsonProcessingException e) {
            throw new TransformerSettingsException(String.format("{\"error\":\"%s\"}", e.getMessage()));
        }

    }


    @Path("sources/{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setSource(@PathParam("id") int sourceId, Source source) {
        try {
            HttpResponse<Source> response = transformerClient.changeSource(sourceId, source);
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
