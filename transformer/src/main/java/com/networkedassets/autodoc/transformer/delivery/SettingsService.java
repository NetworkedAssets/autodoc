package com.networkedassets.autodoc.transformer.delivery;


import com.networkedassets.autodoc.transformer.settings.SettingsForSpace;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.SettingsProvider;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.SettingsSetter;
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
    @Inject private SettingsProvider settingsProvider;
    @Inject private SettingsSetter settingsSetter;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public SettingsForSpace getSettingsForSpace(@QueryParam("spaceKey") String spaceKey,
                                                @QueryParam("confluenceUrl") String confluenceUrl) {
        log.info("GET request for settings handled");
        return settingsProvider.getSettingsForSpace(spaceKey, confluenceUrl);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String setSettingsForSpace(SettingsForSpace settingsForSpace) {
        log.info("POST request for settings handled: " + settingsForSpace.toString());
        settingsSetter.setSettingsForSpace(settingsForSpace, settingsForSpace.getSpaceKey(),
                settingsForSpace.getConfluenceUrl());
        return SUCCESS;
    }


}
