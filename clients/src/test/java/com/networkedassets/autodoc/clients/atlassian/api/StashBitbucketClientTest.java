package com.networkedassets.autodoc.clients.atlassian.api;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.clients.atlassian.HttpClientConfig;
import com.networkedassets.autodoc.clients.atlassian.atlassianHookData.HookSettings;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertNotNull;

public class StashBitbucketClientTest {
	private final String PROJECT_KEY = "TP";
	private final String PROJECT_SLUG = "lol";
	private final String PROJECT_HOOK_KEY = "com.networkedassets.atlasian.plugins.stash-postReceive-hook-plugin:postReceiveHookListener";
	private final String CONFLUENCE_URL = "http://46.101.240.138:7990";
	private final String CONFLUENCE_USERNAME = "kcala";
	private final String CONFLUENCE_PASSWORD = "admin";

	private StashBitbucketClient client;

	@Before
	public void initializeClient() throws MalformedURLException {
		HttpClientConfig httpClientConfig = new HttpClientConfig(new URL(CONFLUENCE_URL), CONFLUENCE_USERNAME,
				CONFLUENCE_PASSWORD);
		client = new StashBitbucketClient(httpClientConfig);
	}

	@Test
	public void ensureClientNotNull() {
		assertNotNull(client);
	}

	@Test(expected = NullPointerException.class)
	public void testGetHookSettingsShouldThrowNullPointerException() throws UnirestException {
		client.getHookSettings(null, null, null);
	}

	

	@Test
	public void testProperArgumentsReturnProperHookSettingsInstance() throws UnirestException {
		HookSettings hookSettings = client.getHookSettings(PROJECT_KEY, PROJECT_SLUG, PROJECT_HOOK_KEY);
		assertNotNull(hookSettings);
	}

	@Test(expected = NullPointerException.class)
	public void testSetHookSettingsShouldThrowNullPointerException() throws UnirestException {
		client.setHookSettings(null, null, null, null, null);
	}

	

	@Test
	public void testSetHookSettingsAndRestoreDefault() throws UnirestException {
		final String testEndpointURL = "http://test.test:1234/test";
		final String defaultEndpointURL = "http://localhost:8050/event";
		final String connTimeout = "30000";
		// test if hook's settings are changed properly
		HookSettings testHookSettings = client.setHookSettings(PROJECT_KEY, PROJECT_SLUG, PROJECT_HOOK_KEY,
				testEndpointURL, connTimeout);
		assertNotNull(testHookSettings);

		// restores default settings
		HookSettings defaultHookSettings = client.setHookSettings(PROJECT_KEY, PROJECT_SLUG, PROJECT_HOOK_KEY,
				defaultEndpointURL, connTimeout);
		assertNotNull(defaultHookSettings);
	}

}
