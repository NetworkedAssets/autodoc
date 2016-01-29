package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;
import com.networkedassets.autodoc.transformer.manageSettings.provide.in.BranchModifier;
import com.networkedassets.autodoc.transformer.manageSettings.provide.in.EventScheduler;
import com.networkedassets.autodoc.transformer.manageSettings.provide.in.SettingsSaver;
import com.networkedassets.autodoc.transformer.manageSettings.provide.out.SettingsProvider;
import com.networkedassets.autodoc.transformer.settings.Branch;
import com.networkedassets.autodoc.transformer.settings.Settings;
import com.networkedassets.autodoc.transformer.settings.view.Views;
import com.networkedassets.autodoc.transformer.util.RestService;

/**
 * REST service providing and receiving global settings
 */

@Path("/settings")
public class SettingsService extends RestService {

    static final Logger log = LoggerFactory.getLogger(SettingsService.class);
    private SettingsProvider settingsProvider;
    private SettingsSaver settingsSaver;

    @Inject
    public SettingsService(SettingsProvider settingsProvider, SettingsSaver settingsSaver) {
        this.settingsProvider = settingsProvider;
        this.settingsSaver = settingsSaver;
    }

    @JsonView(Views.PublicView.class)
    @GET
    public Response getSettings() {
        log.info("GET request for settings handled");

        return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON)
                .entity(settingsProvider.getCurrentSettings()).build();

    }

    @Path("/credentials")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCredentials() {
        //// TODO: 29.01.2016 Don't return null, lol
        return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON)
                .entity(null).build();
    }

    @Path("/credentials")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @JsonView(Views.PublicView.class)
    public Response setCredentials(Settings settings) {
        settingsSaver.setCredentials(settings);
        return Response.status(Response.Status.ACCEPTED).build();
    }
}
