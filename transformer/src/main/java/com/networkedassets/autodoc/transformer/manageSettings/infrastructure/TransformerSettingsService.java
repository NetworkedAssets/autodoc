package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;

import com.networkedassets.autodoc.transformer.manageSettings.provide.in.SettingsSaver;
import com.networkedassets.autodoc.transformer.manageSettings.provide.out.SettingsProvider;
import com.networkedassets.autodoc.transformer.settings.TransformerSettings;
import com.networkedassets.autodoc.transformer.util.RestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * REST service providing and receiving transformer settings
 */

@Path("/settings/transformer")
public class TransformerSettingsService {

    static final Logger log = LoggerFactory.getLogger(TransformerSettingsService.class);
    private SettingsProvider settingsProvider;
    private SettingsSaver settingsSetter;

    @Inject
    public TransformerSettingsService(SettingsProvider settingsProvider, SettingsSaver settingsSetter) {
        this.settingsProvider = settingsProvider;
        this.settingsSetter = settingsSetter;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public TransformerSettings getTransformerSettings(){
        log.info("GET request for transformer settings handled");
        return settingsProvider.getTransformerSettings();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public TransformerSettings setTransformerSettings(TransformerSettings transformerSettings){
        log.info("POST request for transformer settings handled: " + transformerSettings.toString());
        settingsSetter.setTransformerSettings(transformerSettings);
        return settingsProvider.getTransformerSettings();
    }
}
