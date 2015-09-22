package com.networkedassets.autodoc.transformer.clients.atlassian;

import javax.annotation.Nonnull;



public interface HttpClientFactory {
	
	HttpClient getHttpClient(@Nonnull HttpClientConfig config);

}
