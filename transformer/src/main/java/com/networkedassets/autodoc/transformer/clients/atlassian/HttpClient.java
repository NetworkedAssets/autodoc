package com.networkedassets.autodoc.transformer.clients.atlassian;

import java.io.IOException;
import java.net.URL;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;

public class HttpClient {

	@Nonnull
	private final URL baseUrl;
	@Nullable
	private final String username;
	@Nullable
	private final String password;

	public HttpClient(HttpClientConfig config) {

		this.baseUrl = config.getBaseUrl();
		this.username = config.getUsername();
		this.password = config.getPassword();

		Unirest.setObjectMapper(new ObjectMapper() {
			private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

			public <T> T readValue(String value, Class<T> valueType) {
				try {
					return jacksonObjectMapper.readValue(value, valueType);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			public String writeValue(Object value) {
				try {
					return jacksonObjectMapper.writeValueAsString(value);
				} catch (JsonProcessingException e) {
					throw new RuntimeException(e);
				}
			}
		});

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
