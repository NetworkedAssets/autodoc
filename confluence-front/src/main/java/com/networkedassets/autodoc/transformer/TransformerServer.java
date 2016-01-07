package com.networkedassets.autodoc.transformer;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.transformer.settings.Settings;
import com.networkedassets.autodoc.transformer.settings.SettingsException;
import com.networkedassets.autodoc.transformer.settings.Source;
import com.networkedassets.autodoc.transformer.settings.SourceCustomSerializer;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class TransformerServer {
	public static final String SETTINGS = "/settings";
	public static final String SOURCES = "/sources/";
	public static final String EVENT = "/event";
	public static final String EVENT_JSON = "{\"sourceUrl\":\"%s\",\"projectKey\":\"%s\",\"repositorySlug\":\"%s\",\"branchId\":\"%s\"}";
	public static final Logger log = LoggerFactory.getLogger(TransformerServer.class);
	private static final CloseableHttpClient HTTP_CLIENT = setHttpClient();
	private static final ObjectMapper OBJECT_MAPPER = setObjectMapper();

	private String url;
	private String confluenceUrl;

	public TransformerServer(String url) {
		log.debug("Transformer server constructing");

		this.url = url;

		Unirest.setObjectMapper(OBJECT_MAPPER);
		Unirest.setHttpClient(HTTP_CLIENT);

	}

	public TransformerServer(String transformerUrl, String confluenceUrl) {
		this(transformerUrl);
		setConfluenceUrl(confluenceUrl);
	}

	public Settings getSettings() throws SettingsException {
		HttpResponse<Settings> response;
		try {
			response = Unirest.get(url + SETTINGS).asObject(Settings.class);
		} catch (UnirestException e) {
			throw new SettingsException(e);
		}

		return response.getBody();
	}

	public HttpResponse<String> saveSettingsForSpace(Settings settings) throws SettingsException {
		HttpResponse<String> response;
		try {

			response = Unirest.post(url + SETTINGS).header("Content-Type", "application/json").body(settings)
					.asString();
		} catch (UnirestException e) {
			throw new SettingsException(e);
		}

		if (response.getStatus() != 200) {
			throw new SettingsException("Could not save settings: " + response.getBody());
		}

		return response;
	}

	public HttpResponse<String> forceRegenerate(String sourceUrl, String projectKey, String repoSlug, String branchId)
			throws SettingsException {
		String eventPayload = String.format(EVENT_JSON, sourceUrl, projectKey, repoSlug, branchId);
		HttpResponse<String> response;
		try {

			response = Unirest.post(url + EVENT).header("Content-Type", "application/json").body(eventPayload)
					.asString();
		} catch (UnirestException e) {
			throw new SettingsException(e);
		}

		if (response.getStatus() != 200) {
			throw new SettingsException("Error in transformer: " + response);
		}
		return response;
	}

	public Source getSource(String id) throws SettingsException {
		HttpResponse<Source> response;
		try {

			response = Unirest.get(url + SOURCES + "{id}").routeParam("id", id).asObject(Source.class);
		} catch (UnirestException e) {
			throw new SettingsException(e);
		}

		return response.getBody();

	}

	public HttpResponse<Source> setSource(Source source) throws SettingsException {

		HttpResponse<Source> response;
		try {

			response = Unirest.post(url + SOURCES).header("Content-Type", "application/json").body(source)
					.asObject(Source.class);
		} catch (UnirestException e) {
			throw new SettingsException(e);
		}

		return response;

	}

	public HttpResponse<Source> changeSource(int sourceId, Source source) throws SettingsException {

		HttpResponse<Source> response;
		try {

			response = Unirest.put(url + SOURCES + String.valueOf(sourceId)).header("Content-Type", "application/json")
					.body(source).asObject(Source.class);
		} catch (UnirestException e) {
			throw new SettingsException(e);
		}

		return response;

	}

	public HttpResponse<String> removeSource(int sourceId) throws SettingsException {

		HttpResponse<String> response;
		try {

			response = Unirest.delete(url + SOURCES + String.valueOf(sourceId)).asString();
		} catch (UnirestException e) {
			throw new SettingsException(e);
		}

		return response;

	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setConfluenceUrl(String confluenceUrl) {
		this.confluenceUrl = confluenceUrl;
	}

	private static CloseableHttpClient setHttpClient() {
		try {
			return HttpClients.custom().setHostnameVerifier(new AllowAllHostnameVerifier())
					.setSslcontext(new SSLContextBuilder().loadTrustMaterial(null, (_1, _2) -> true).build()).build();
		} catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
			throw new RuntimeException(e);
		}
	}

	private static ObjectMapper setObjectMapper() {

		return new ObjectMapper() {
			private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

			@Override
			public <T> T readValue(String value, Class<T> valueType) {
				try {
					return jacksonObjectMapper.readValue(value, valueType);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public String writeValue(Object value) {
				try {

					SimpleModule module = new SimpleModule();
					module.addSerializer(new SourceCustomSerializer(Source.class));
					jacksonObjectMapper.registerModule(module);
					return jacksonObjectMapper.writeValueAsString(value);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};

	}
}
