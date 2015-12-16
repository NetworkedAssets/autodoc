package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.networkedassets.autodoc.transformer.manageSettings.provide.out.SettingsProvider;
import com.networkedassets.autodoc.transformer.settings.Source;
import com.networkedassets.autodoc.transformer.settings.SourceCheckingDeserializer;
import com.networkedassets.autodoc.transformer.settings.SourceCheckingSerializer;
import com.networkedassets.autodoc.transformer.util.RestErrorMessage;
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

    private SettingsProvider settingsProvider;

    @Inject
    public SourceService(SettingsProvider settingsProvider) {
        this.settingsProvider = settingsProvider;
    }

    static final Logger log = LoggerFactory.getLogger(SourceService.class);

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSource(@PathParam("id") int sourceId) {
        log.info("GET request for source handled");
        Optional<Source> requestedSource = settingsProvider.getCurrentSettings().getSources().stream()
                .filter(source -> source.getId()==sourceId)
                .findFirst();

        if (!requestedSource.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.status(Response.Status.OK)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(requestedSource.orElse(null))
                    .build();
        }

    }




}
