package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;

import com.networkedassets.autodoc.clients.atlassian.HttpClientConfig;
import com.networkedassets.autodoc.clients.atlassian.api.StashClient;
import com.networkedassets.autodoc.transformer.settings.TransformerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by kamil on 18.11.2015.
 */
public class StashClientConfigurator {

    private static Logger log = LoggerFactory.getLogger(StashClientConfigurator.class);
    private static StashClient stashClient;

    public static StashClient getConfiguredStashClient(TransformerSettings transformerSettings) throws MalformedURLException {
        if(stashClient==null){
            URL stashUrl;
            stashUrl = new URL(transformerSettings.getStashUrl());
            stashClient = new StashClient(
                    new HttpClientConfig(
                            stashUrl,
                            transformerSettings.getStashUsername(),
                            transformerSettings.getStashPassword()));
            log.debug("Stash client created");
        }
        return stashClient;
    }

}

