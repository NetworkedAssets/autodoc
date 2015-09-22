package com.networkedassets.autodoc.transformer.clients.atlassian.api;

import com.networkedassets.autodoc.transformer.clients.atlassian.HttpClient;
import com.networkedassets.autodoc.transformer.clients.atlassian.HttpClientConfig;
import com.networkedassets.autodoc.transformer.clients.atlassian.HttpClientFactory;

public class StashClientFactory implements HttpClientFactory {

	public HttpClient getHttpClient(HttpClientConfig config) {

		return new StashClient(config);
	}

}
