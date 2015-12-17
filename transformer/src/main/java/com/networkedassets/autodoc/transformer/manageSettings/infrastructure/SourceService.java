package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;

import com.google.common.base.Preconditions;
import com.networkedassets.autodoc.transformer.manageSettings.provide.in.SourceCreator;
import com.networkedassets.autodoc.transformer.manageSettings.provide.out.SourceProvider;
import com.networkedassets.autodoc.transformer.settings.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

/**
 * REST service intended to check whether given source exists
 */

@Path("/sources/")
public class SourceService {

    static final Logger log = LoggerFactory.getLogger(SourceService.class);
    private SourceProvider sourceProvider;
    private SourceCreator sourceCreator;


    @Inject
    public SourceService(SourceProvider sourceProvider, SourceCreator sourceCreator) {
        this.sourceProvider = sourceProvider;
        this.sourceCreator = sourceCreator;
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSource(@PathParam("id") int sourceId) {
        log.info("GET request for source handled");
        Optional<Source> requestedSource = sourceProvider.getSourceById(sourceId);

        return requestedSource.map(s ->
                Response.status(Response.Status.OK)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(s)
                        .build()
        ).orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSource(Source source) {
        Preconditions.checkNotNull(source);
        log.info("POST request for source handled");
        Source resultSource = sourceCreator.createSource(source);
        return null;
    }



}
