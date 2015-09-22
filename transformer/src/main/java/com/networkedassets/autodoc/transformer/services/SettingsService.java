package com.networkedassets.autodoc.transformer.services;


import com.networkedassets.autodoc.transformer.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * REST service providing and receiving settings
 */

@Path("/settings")
public class SettingsService extends RestService {

    static final Logger log = LoggerFactory.getLogger(SettingsService.class);
    private Settings settings = new Settings();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Settings getSettings() {
        log.info("GET request for settings handled");
        //TODO return settings as json
        return settings;

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String setSettings(Settings settings) {
        log.info("POST request for settings handled: " + settings.toString());
        //TODO set received settings
        this.settings = settings;
        return SUCCESS;
    }


}
