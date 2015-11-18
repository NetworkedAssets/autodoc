package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.clients.atlassian.api.StashClient;
import com.networkedassets.autodoc.transformer.manageSettings.require.HookActivator;
import com.networkedassets.autodoc.transformer.settings.Project;
import com.networkedassets.autodoc.transformer.settings.TransformerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.Map;

/**
 * Created by kamil on 18.11.2015.
 */
public class StashHookActivator implements HookActivator {

    private static Logger log = LoggerFactory.getLogger(StashHookActivator.class);

    StashClient stashClient;
    TransformerSettings transformerSettings;

    public StashHookActivator(TransformerSettings transformerSettings) throws MalformedURLException {
        this.transformerSettings = transformerSettings;
        stashClient = StashClientConfigurator.getConfiguredStashClient(transformerSettings);
    }

    @Override
    public void enableAllHooks(Map<String, Project> projects) {
        projects.values().stream().forEach(project -> project.repos.values().stream().forEach(repo -> {
            try {
                stashClient.setHookSettings(
                        project.key,
                        repo.slug,
                        transformerSettings.getStashHookKey(),
                        transformerSettings.getLocalhostAddress(),
                        "30000");
            } catch (UnirestException e) {
                log.error("Error while activating hooks for {}/{}: ", project.name, repo.slug, e);
            }
        }));
    }
}
