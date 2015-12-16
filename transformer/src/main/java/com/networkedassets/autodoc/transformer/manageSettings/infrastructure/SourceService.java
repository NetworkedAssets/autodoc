package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;

import com.networkedassets.autodoc.transformer.manageSettings.provide.out.SettingsProvider;
import com.networkedassets.autodoc.transformer.settings.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * REST service intended to check whether given source exists
 */

@Path("/source")
public class SourceService {

    private SettingsProvider settingsProvider;

    @Inject
    public SourceService(SettingsProvider settingsProvider) {
        this.settingsProvider = settingsProvider;
    }

    static final Logger log = LoggerFactory.getLogger(SourceService.class);

    // TODO: 16.12.2015 IMPLEMENT
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Source getSource() {
        log.info("GET request for source handled");
        settingsProvider.getCurrentSettings().getSources();
        return null;
    }
}
