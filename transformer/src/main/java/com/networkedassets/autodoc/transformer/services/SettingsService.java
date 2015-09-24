package com.networkedassets.autodoc.transformer.services;


import com.networkedassets.autodoc.transformer.SettingsManager;
import com.networkedassets.autodoc.transformer.settings.SettingsForSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * REST service providing and receiving settings
 */

@Path("/settings")
public class SettingsService extends RestService {

    static final Logger log = LoggerFactory.getLogger(SettingsService.class);
    @Inject private SettingsManager settingsManager;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public SettingsForSpace getSettingsForSpace(@QueryParam("spaceKey") String spaceKey,
                                                @QueryParam("confluenceBaseUrl") String confluenceBaseUrl) {
        log.info("GET request for settings handled");
        SettingsForSpace settingsForSpace = settingsManager.getSettingsForSpace(spaceKey, confluenceBaseUrl);
        return settingsForSpace;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String setSettingsForSpace(SettingsForSpace settingsForSpace) {
        log.info("POST request for settings handled: " + settingsForSpace.toString());
        settingsManager.setSettingsForSpace(settingsForSpace, settingsForSpace.getSpaceKey(),
                settingsForSpace.getConfluenceUrl());
        return SUCCESS;
    }


}
