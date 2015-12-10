package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.clients.atlassian.api.BitbucketClient;
import com.networkedassets.autodoc.clients.atlassian.api.StashClient;
import com.networkedassets.autodoc.transformer.manageSettings.require.HookActivator;
import com.networkedassets.autodoc.transformer.settings.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

/**
 * Created by kamil on 18.11.2015.
 */
public class BitbucketHookActivator implements HookActivator {

    private static Logger log = LoggerFactory.getLogger(BitbucketHookActivator.class);

    BitbucketClient bitbucketClient;
    Source source;
    String localhostAddress;

    public BitbucketHookActivator(Source source, String localhostAddress) throws MalformedURLException {
        this.localhostAddress = localhostAddress;
        this.source = source;
        bitbucketClient = ClientConfigurator.getConfiguredBitbucketClient(source);
    }


    @SuppressWarnings("Duplicates")
    @Override
    public void enableAllHooks() {
        source.projects.values().stream().forEach(project -> project.repos.values().stream().forEach(repo -> {
            try {
                bitbucketClient.setHookSettings(
                        project.key,
                        repo.slug,
                        source.getHookKey(),
                        localhostAddress,
                        "30000");
                bitbucketClient.setHookSettingsEnabled(
                        project.key,
                        repo.slug,
                        source.getHookKey()
                );
            } catch (UnirestException e) {
                log.error("Error while activating hooks for {}/{}: ", project.name, repo.slug, e);
            }
        }));
    }
}
