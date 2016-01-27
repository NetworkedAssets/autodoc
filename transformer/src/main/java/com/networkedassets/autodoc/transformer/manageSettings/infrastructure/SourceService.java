package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Preconditions;
import com.networkedassets.autodoc.transformer.manageSettings.provide.in.SourceCreator;
import com.networkedassets.autodoc.transformer.manageSettings.provide.in.SourceModifier;
import com.networkedassets.autodoc.transformer.manageSettings.provide.in.SourceRemover;
import com.networkedassets.autodoc.transformer.manageSettings.provide.out.SourceProvider;
import com.networkedassets.autodoc.transformer.settings.Source;
import com.networkedassets.autodoc.transformer.settings.view.Views;
import com.networkedassets.util.functional.Throwing;

/**
 * REST service intended to check whether given source exists
 */

@Path("/sources/")
public class SourceService {

    static final Logger log = LoggerFactory.getLogger(SourceService.class);
    private SourceProvider sourceProvider;
    private SourceCreator sourceCreator;
    private SourceRemover sourceRemover;
    private SourceModifier sourceModifier;
    private static final ObjectMapper OBJECT_MAPPER = setObjectMapper();

    @Inject
    public SourceService(SourceProvider sourceProvider, SourceCreator sourceCreator, SourceRemover sourceRemover,
                         SourceModifier sourceModifier) {
        this.sourceProvider = sourceProvider;
        this.sourceCreator = sourceCreator;
        this.sourceRemover = sourceRemover;
        this.sourceModifier = sourceModifier;
    }

    //TODO: Add rest for source with projects

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSources() {
        log.info("GET request for source handled");

        List<String> sources = sourceProvider.getAllSources()
                .stream().map(Throwing.rethrowAsRuntimeException(s -> OBJECT_MAPPER
                        .writerWithView(Views.PublicView.class).writeValueAsString(s)))
                .collect(Collectors.toList());

        return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(sources).build();
    }

    //TODO:add Object mapper writer with view

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSource(@PathParam("id") int sourceId) {
        log.info("GET request for source handled");
        Optional<Source> requestedSource = sourceProvider.getSourceById(sourceId);

        return requestedSource
                .map(s -> Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(s).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }


    //TODO:add Object mapper writer with view
    @PUT
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifySource(@PathParam("id") int sourceId, Source source) {
        Preconditions.checkNotNull(source);
        log.info("PUT request for source handled");
        Response.Status responseStatus;
        //is given id present in settings? This endpoint method can't create new sources!
        if (sourceProvider.getSourceById(sourceId).isPresent()) {
            source.setId(sourceId);
            Source resultSource = sourceModifier.modifySource(source);
            if (resultSource.isCorrect()) {
                responseStatus = Response.Status.OK;
            } else {
                responseStatus = Response.Status.BAD_REQUEST;
            }
            return Response.status(responseStatus).entity(resultSource).build();
        } else {
            responseStatus = Response.Status.BAD_REQUEST;
            return Response.status(responseStatus).build();
        }
    }

    //TODO: add json view
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSource(Source source) {
        Preconditions.checkNotNull(source);
        log.info("POST request for source handled");
        Source resultSource = sourceCreator.addSource(source);
        Response.Status responseStatus;
        if (resultSource.isCorrect()) {
            responseStatus = Response.Status.CREATED;
        } else {
            responseStatus = Response.Status.BAD_REQUEST;
        }
        return Response.status(responseStatus).entity(resultSource).build();
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeSource(@PathParam("id") int sourceId) {

        log.info("DELETE request for source handled");
        boolean wasDeleted = sourceRemover.removeSource(sourceId);
        Response.Status responseStatus = wasDeleted ? Response.Status.ACCEPTED : Response.Status.BAD_REQUEST;
        return Response.status(responseStatus).build();
    }

    private static ObjectMapper setObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return objectMapper;
    }

}
