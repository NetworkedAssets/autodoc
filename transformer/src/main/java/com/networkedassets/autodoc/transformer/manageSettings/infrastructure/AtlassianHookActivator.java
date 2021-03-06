package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.clients.atlassian.api.StashBitbucketClient;
import com.networkedassets.autodoc.transformer.manageSettings.require.HookActivator;
import com.networkedassets.autodoc.transformer.settings.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;


public class AtlassianHookActivator implements HookActivator {

    private static Logger log = LoggerFactory.getLogger(AtlassianHookActivator.class);

    StashBitbucketClient stashBitbucketClient;
    Source source;
    String localhostAddress;

    public AtlassianHookActivator(Source source, String localhostAddress) throws MalformedURLException {
        this.localhostAddress = localhostAddress;
        this.source = source;
        stashBitbucketClient = ClientFactory.getConfiguredStashBitbucketClient(source);
    }


    @Override
    public void enableAllHooks() {
        source.getProjects().values().stream().forEach(project -> project.getRepos().values().stream().forEach(repo -> {
            try {
                stashBitbucketClient.setHookSettings(
                        project.getKey(),
                        repo.getSlug(),
                        source.getHookKey(),
                        localhostAddress,
                        "30000");
                stashBitbucketClient.enableHook(
                        project.getKey(),
                        repo.getSlug(),
                        source.getHookKey()
                );

            } catch (UnirestException e) {
                log.error("Error while activating hooks for {}/{}: ", project.getName(), repo.getSlug(), e);
            }
        }));
    }
}
