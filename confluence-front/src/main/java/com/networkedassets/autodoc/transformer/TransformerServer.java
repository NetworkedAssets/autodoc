package com.networkedassets.autodoc.transformer;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.commons.io.IOUtils;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.transformer.settings.Settings;
import com.networkedassets.autodoc.transformer.settings.SettingsException;

public class TransformerServer {
	public static final String SETTINGS = "/settings";
	public static final String EVENT = "/event";
	public static final String EVENT_JSON = "{\"repositorySlug\":\"%s\",\"projectKey\":\"%s\",\"changes\":[{\"refId\":\"%s\",\"type\":\"UPDATE\"}]}";

	public static final Logger log = LoggerFactory.getLogger(TransformerServer.class);

	private String url;
	private String confluenceUrl;

	public TransformerServer(String url) {
		log.debug("Transformer server constructing");

		this.url = url;

		Unirest.setObjectMapper(new ObjectMapper() {
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
					return jacksonObjectMapper.writeValueAsString(value);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	public TransformerServer(String transformerUrl, String confluenceUrl) {
		this(transformerUrl);
		setConfluenceUrl(confluenceUrl);
	}

	public Response getSettings() throws SettingsException {
		HttpResponse<Settings> response;
		String raw;
		try {
			Unirest.setHttpClient(setCustumHttpClient());
			response = Unirest.get(url + SETTINGS).asObject(Settings.class);
			raw = IOUtils.toString(response.getRawBody());
		} catch (UnirestException e) {
			throw new SettingsException(e);
		} catch (IOException | KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			throw new RuntimeException(e);
		}

		return new Response(response.getBody(), raw);
	}

	public HttpResponse<String> saveSettingsForSpace(Settings settings) throws SettingsException {
		HttpResponse<String> response;
		try {
			Unirest.setHttpClient(setCustumHttpClient());
			response = Unirest.post(url + SETTINGS).header("Content-Type", "application/json").body(settings)
					.asString();
		} catch (UnirestException e) {
			throw new SettingsException(e);

		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			throw new RuntimeException(e);
		}

		if (response.getStatus() != 200) {
			throw new SettingsException("Could not save settings: " + response.getBody());
		}

		return response;
	}

	public HttpResponse<String> forceRegenerate(String projectKey, String repoSlug, String branchId)
			throws SettingsException {
		String eventPayload = String.format(EVENT_JSON, repoSlug, projectKey, branchId);
		HttpResponse<String> response;
		try {
			Unirest.setHttpClient(setCustumHttpClient());
			response = Unirest.post(url + EVENT).header("Content-Type", "application/json").body(eventPayload)
					.asString();
		} catch (UnirestException e) {
			throw new SettingsException(e);
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			throw new RuntimeException(e);
		}

		if (response.getStatus() != 200) {
			throw new SettingsException("Error in transformer: " + response);
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

	private CloseableHttpClient setCustumHttpClient()
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

		CloseableHttpClient httpClient = HttpClients.custom().setHostnameVerifier(new AllowAllHostnameVerifier())
				.setSslcontext(new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
					public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
						return true;
					}
				}).build()).build();
		return httpClient;

	}

}
