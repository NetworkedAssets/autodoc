package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;

import com.google.common.base.Preconditions;
import com.networkedassets.autodoc.transformer.manageSettings.provide.in.SourceChanger;
import com.networkedassets.autodoc.transformer.manageSettings.provide.in.SourceCreator;
import com.networkedassets.autodoc.transformer.manageSettings.provide.in.SourceRemover;
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
    private SourceRemover sourceRemover;
    private SourceChanger sourceChanger;

    @Inject
    public SourceService(SourceProvider sourceProvider, SourceCreator sourceCreator, SourceRemover sourceRemover, SourceChanger sourceChanger) {
        this.sourceProvider = sourceProvider;
        this.sourceCreator = sourceCreator;
        this.sourceRemover = sourceRemover;
        this.sourceChanger = sourceChanger;
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
    
    @PUT
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeSource(@PathParam("id") int sourceId, Source source) {
    	 Preconditions.checkNotNull(source);
        //TODO: check if source id exists
         log.info("PUT request for source handled");
         source.setId(sourceId);
         Source resultSource = sourceChanger.changeSource(source);
         Response.Status responseStatus;
         if(resultSource.isCorrect()){
             responseStatus = Response.Status.CREATED;
         } else{
             responseStatus = Response.Status.BAD_REQUEST;
         }
         return Response.status(responseStatus)
                 .entity(resultSource)
                 .build();
     }
    
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSource(Source source) {
        Preconditions.checkNotNull(source);
        log.info("POST request for source handled");
        Source resultSource = sourceCreator.addSource(source);
        Response.Status responseStatus;
        if(resultSource.isCorrect()){
            responseStatus = Response.Status.CREATED;
        } else{
            responseStatus = Response.Status.BAD_REQUEST;
        }
        return Response.status(responseStatus)
                .entity(resultSource)
                .build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeSource(Source source){
    	
    	//TODO: pass id not source
        Preconditions.checkNotNull(source);
        log.info("DELETE request for source handled");
        boolean wasDeleted = sourceRemover.removeSource(source);
        Response.Status responseStatus = wasDeleted ? Response.Status.ACCEPTED : Response.Status.BAD_REQUEST;
        return Response.status(responseStatus).build();
    }


}
