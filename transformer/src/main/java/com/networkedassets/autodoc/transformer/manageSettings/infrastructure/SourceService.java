package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networkedassets.autodoc.transformer.manageSettings.provide.in.*;
import com.networkedassets.autodoc.transformer.settings.Branch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.Preconditions;
import com.networkedassets.autodoc.transformer.manageSettings.provide.out.SourceProvider;
import com.networkedassets.autodoc.transformer.settings.Source;
import com.networkedassets.autodoc.transformer.settings.view.Views;

/**
 * * REST service providing and receiving source specific settings
 */

@Path("/sources/")
public class SourceService {


    static final Logger log = LoggerFactory.getLogger(SourceService.class);
    private SourceProvider sourceProvider;
    private SourceCreator sourceCreator;
    private SourceRemover sourceRemover;
    private SourceModifier sourceModifier;
    private EventScheduler eventScheduler;
    private BranchModifier branchModifier;

    @Inject
    public SourceService(SourceProvider sourceProvider, SourceCreator sourceCreator, SourceRemover sourceRemover, SourceModifier sourceModifier, EventScheduler eventScheduler, BranchModifier branchModifier) {
        this.sourceProvider = sourceProvider;
        this.sourceCreator = sourceCreator;
        this.sourceRemover = sourceRemover;
        this.sourceModifier = sourceModifier;
        this.eventScheduler = eventScheduler;
        this.branchModifier = branchModifier;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(Views.GetSourcesView.class)
    public Response getSources() {
        log.info("GET request for source handled");

        List<Source> sources = sourceProvider.getAllSources();
        return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(sources).build();
    }


    @GET
    @Path("extended")
    @JsonView(Views.GetExpandedSourcesView.class)
    public Response getExtendedSources() {
        log.info("GET request for expanded sources handled");
        List<Source> sources = sourceProvider.getAllSources();
        return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(sources).build();
    }

    @GET
    @Path("{id}")
    @JsonView(Views.GetExpandedSourcesView.class)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSource(@PathParam("id") int sourceId) {
        log.info("GET request for source handled");
        Optional<Source> requestedSource = sourceProvider.getSourceById(sourceId);

        return requestedSource
                .map(s -> Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(s).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @PUT
    @Path("{id}")
    @JsonView(Views.AddSourcePasswordView.class)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifySource(@PathParam("id") int sourceId, Source source) throws JsonProcessingException {
        Preconditions.checkNotNull(source);
        log.info("PUT request for source handled");
        Response.Status responseStatus;
        // is given id present in settings? This endpoint method can't create
        // new sources!
        if (sourceProvider.getSourceById(sourceId).isPresent()) {
            source.setId(sourceId);
            Source resultSource = sourceModifier.modifySource(source);
            if (resultSource.isCorrect()) {
                responseStatus = Response.Status.OK;
            } else {
                responseStatus = Response.Status.BAD_REQUEST;
            }
            return Response.status(responseStatus).entity(getSourceWithProjectReturnView(resultSource)).build();
        } else {
            responseStatus = Response.Status.BAD_REQUEST;
            return Response.status(responseStatus).build();
        }
    }

    @POST
    @JsonView(Views.AddSourcePasswordView.class)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSource(Source source) throws JsonProcessingException {

        Preconditions.checkNotNull(source);
        log.info("POST request for source handled");
        Source resultSource = sourceCreator.addSource(source);
        Response.Status responseStatus;
        if (resultSource.isCorrect()) {
            responseStatus = Response.Status.CREATED;
        } else {
            responseStatus = Response.Status.BAD_REQUEST;
        }

        return Response.status(responseStatus).entity(getSourceWithProjectReturnView(resultSource)).build();
    }

    @DELETE
    @Path("{id}")
    @JsonView(Views.AddSourcePasswordView.class)
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeSource(@PathParam("id") int sourceId) {

        log.info("DELETE request for source handled");
        boolean wasDeleted = sourceRemover.removeSource(sourceId);
        Response.Status responseStatus = wasDeleted ? Response.Status.ACCEPTED : Response.Status.BAD_REQUEST;
        return Response.status(responseStatus).build();
    }

    @PUT
    @Path("{sourceId}/{projectKey}/{repoSlug}/{branchId}")
    @JsonView(Views.GetExpandedSourcesView.class)
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

    private String getSourceWithProjectReturnView(Source resultSource) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writerWithView(Views.AddSourceReturnView.class)
                .forType(Source.class).writeValueAsString(resultSource);
    }

}
