package com.networkedassets.autodoc.services;

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
    public static String getSettings() {
        log.info("GET request for settings handled");
        //TODO return settings as json
        return "settings will be there as JSON";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public static String setSettings() {
        log.info("POST request for settings handled");
        //TODO set received settings
        return SUCCESS;
    }


}
