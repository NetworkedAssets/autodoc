package com.networkedassets.autodoc.clients.atlassian.api;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.clients.atlassian.HttpClientConfig;
import com.networkedassets.autodoc.clients.atlassian.stashData.Branch;
import com.networkedassets.autodoc.clients.atlassian.stashData.HookSettings;
import com.networkedassets.autodoc.clients.atlassian.stashData.Repository;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by mtulaza on 2015-12-14.
 */
// TODO: appropriate hookKey for stash is required, then remove @Ignore annotations
public class StashClientTest {
    private final String BITBUCKET_URL = "http://46.101.240.138:7990";
    private final String BITBUCKET_USERNAME = "kcala";
    private final String BITBUCKET_PASSWORD = "admin";

    private final String PROJECT_KEY = "TP";
    private final String REPO_SLUG = "lol";

    private StashClient stashClient;

    public StashClientTest() throws MalformedURLException {
        HttpClientConfig httpClientConfig = new HttpClientConfig(
                new URL(BITBUCKET_URL), BITBUCKET_USERNAME, BITBUCKET_PASSWORD
        );
        this.stashClient = new StashClient(httpClientConfig);
    }

    @Test
    public void testBitbucketClient() {
        assertNotNull(stashClient);

        assertNotNull(stashClient.getBaseUrl());
        assertNotEquals(stashClient.getBaseUrl().toString().trim(), "");
    }

    @Test
    public void testGetRepositories() throws UnirestException {
        List<Repository> repoList = stashClient.getRepositories(null, null, 0, 9999).getBody().getValues();
        assertNotNull(repoList);
        repoList.forEach(Assert::assertNotNull);

        assertNotNull(stashClient.getRepositories(PROJECT_KEY, null, 0, 9999).getBody().getValues());
    }

    @Test
    @Ignore
    public void testGetHookSettings() throws UnirestException {
        HttpResponse<HookSettings> response = stashClient
                .getHookSettings(PROJECT_KEY, REPO_SLUG, "com.networkedassets.atlassian.plugins.stash-postReceive-hook-plugin:postReceiveHookListener");
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertNotNull(response.getHeaders());
        assertNotNull(response.getStatus());
        assertNotNull(response.getRawBody());
        assertNotNull(response.getStatusText());

        assertNotNull(response.getBody().getUrl());
    }

    @Test
    public void testGetRepositoryBranches() throws UnirestException {
        List<Branch> branchList = stashClient.getRepositoryBranches(PROJECT_KEY, REPO_SLUG, null, 0, 9999).getBody().getValues();
        assertNotNull(branchList);
        assertNotNull(stashClient.getRepositoryBranches(null, null, null, 0, 9999).getBody().getValues());
        branchList.forEach(Assert::assertNotNull);
    }

    @Test
    @Ignore
    public void testSetHookSettingsAndRestoreDefault() throws UnirestException {
        final String testEndpointURL = "http://test.test:1234/test";
        final String defaultEndpointURL = "http://localhost:8050/event";
        final String connTimeout = "30000";
        // test if hook's settings are changed properly
        HttpResponse<HookSettings> testResponse = stashClient.setHookSettings(PROJECT_KEY, REPO_SLUG, "com.networkedassets.atlassian.plugins.stash-postReceive-hook-plugin:postReceiveHookListener",
                testEndpointURL, connTimeout);
        assertNotNull(testResponse.getBody());

        // restores default settings
        HttpResponse<HookSettings> defaultResponse = stashClient.setHookSettings(PROJECT_KEY, REPO_SLUG, "com.networkedassets.atlassian.plugins.stash-postReceive-hook-plugin:postReceiveHookListener",
                defaultEndpointURL, connTimeout);
        assertNotNull(defaultResponse.getBody());
    }

    @Test
    @Ignore
    public void testSetHookSettingsDisabledEnabledAndRestoreDefault() throws UnirestException {
        // test
        stashClient.setHookSettingsDisabled(PROJECT_KEY, REPO_SLUG, "com.networkedassets.atlassian.plugins.stash-postReceive-hook-plugin:postReceiveHookListener");
        // restore default
        stashClient.setHookSettingsEnabled(PROJECT_KEY, REPO_SLUG, "com.networkedassets.atlassian.plugins.stash-postReceive-hook-plugin:postReceiveHookListener");
    }
}
