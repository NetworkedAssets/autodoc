package com.networkedassets.autodoc.transformer.clients.atlassian;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URL;

public class HttpClientConfig {

	@Nonnull
	private final URL baseUrl;
	@Nullable
	private final String username;
	@Nullable
	private final String password;

	public HttpClientConfig(@Nonnull URL baseUrl, @Nullable String username,
			@Nullable String password) {
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
	}

	@Nonnull
	public URL getBaseUrl() {
		return baseUrl;
	}

	@Nullable
	public String getUsername() {
		return username;
	}

	@Nullable
	public String getPassword() {
		return password;
	}
}
