package com.networkedassets.autodoc.services;

import com.networkedassets.autodoc.Settings;
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


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public static Settings getSettings() {
        log.info("GET request for settings handled");
        //TODO return settings as json
        return new Settings();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public static String setSettings(Settings settings) {
        log.info("POST request for settings handled: " + settings.toString());
        //TODO set received settings
        return SUCCESS;
    }


}
