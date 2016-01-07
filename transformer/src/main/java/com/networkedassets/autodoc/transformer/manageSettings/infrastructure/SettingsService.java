package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;

import com.networkedassets.autodoc.transformer.manageSettings.provide.in.BranchModifier;
import com.networkedassets.autodoc.transformer.manageSettings.provide.out.SettingsProvider;
import com.networkedassets.autodoc.transformer.settings.Branch;
import com.networkedassets.autodoc.transformer.settings.Settings;
import com.networkedassets.autodoc.transformer.util.RestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * REST service providing and receiving settings
 */

@Path("/settings")
public class SettingsService extends RestService {

    static final Logger log = LoggerFactory.getLogger(SettingsService.class);
    private SettingsProvider settingsProvider;
    private BranchModifier branchModifier;

    @Inject
    public SettingsService(SettingsProvider settingsProvider, BranchModifier branchModifier) {
        this.settingsProvider = settingsProvider;
        this.branchModifier = branchModifier;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Settings getSettings() {
        log.info("GET request for settings handled");
        return settingsProvider.getCurrentSettings();
    }

    // TODO: implement
    @POST
    @Path("/branches/{sourceId}/{projectKey}/{repoSlug}/{branchId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyBranch(@PathParam("sourceId") int sourceId, @PathParam("projectKey") String projectKey,
                                 @PathParam("repoSlug") String repoSlug, @PathParam("branchId") String branchId, Branch branch) {
        try {
            projectKey = URLDecoder.decode(projectKey, "UTF-8");
            repoSlug = URLDecoder.decode(repoSlug, "UTF-8");
            branchId = URLDecoder.decode(branchId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Branch modifiedBranch = branchModifier.modifyBranch(sourceId, projectKey, repoSlug, branchId, branch);
        if (modifiedBranch != null) {
            return Response.status(Response.Status.OK).entity(modifiedBranch).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Wrong parameters").build();
        }

    }
}
