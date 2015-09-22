package com.networkedassets.autodoc.transformer.clients.atlassian.api;

import com.networkedassets.autodoc.transformer.clients.atlassian.HttpClient;
import com.networkedassets.autodoc.transformer.clients.atlassian.HttpClientConfig;
import com.networkedassets.autodoc.transformer.clients.atlassian.HttpClientFactory;

public class ConfluenceClientFactory implements HttpClientFactory {

	

	public HttpClient getHttpClient(HttpClientConfig config) {
		
		 return new ConfluenceClient(config);
	}

}
