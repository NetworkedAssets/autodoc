package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;

import com.networkedassets.autodoc.clients.atlassian.HttpClientConfig;
import com.networkedassets.autodoc.clients.atlassian.api.StashBitbucketClient;
import com.networkedassets.autodoc.transformer.settings.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class ClientFactory {

    private static Logger log = LoggerFactory.getLogger(ClientFactory.class);

    public static StashBitbucketClient getConfiguredStashBitbucketClient(Source source) throws MalformedURLException {
        URL stashUrl = new URL(source.getUrl());
        StashBitbucketClient stashBitbucketClient = new StashBitbucketClient(
                new HttpClientConfig(
                        stashUrl,
                        source.getUsername(),
                        source.getPassword()));
        log.debug("Stash client created");
        return stashBitbucketClient;
    }
}

