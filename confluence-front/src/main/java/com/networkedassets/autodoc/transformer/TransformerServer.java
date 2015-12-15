package com.networkedassets.autodoc.transformer;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.transformer.settings.Settings;
import com.networkedassets.autodoc.transformer.settings.SettingsException;
import org.apache.commons.io.IOUtils;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

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
		try {
			CloseableHttpClient httpClient = null;
			try {
				httpClient = HttpClients.custom().setHostnameVerifier(new AllowAllHostnameVerifier())
						.setSslcontext(new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
							public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
								return true;
							}
						}).build()).build();
			} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
				log.error("Problem with create custom httpClient", e);
			}

			Unirest.setHttpClient(httpClient);
			
			
			response = Unirest.get(url + SETTINGS).asObject(Settings.class);
		} catch (UnirestException e) {
			throw new SettingsException(e);
		}

		
		String raw;
		try {
			raw = IOUtils.toString(response.getRawBody());
			log.warn("response: " + IOUtils.toString(response.getRawBody()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return new Response(response.getBody(), raw);
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

	public void forceRegenerate(String projectKey, String repoSlug, String branchId) throws SettingsException {
		String eventPayload = String.format(EVENT_JSON, repoSlug, projectKey, branchId);
		String response = null;
		try {
			response = Unirest.post(url + EVENT).header("Content-Type", "application/json").body(eventPayload)
					.asString().getBody();
		} catch (UnirestException e) {
			throw new SettingsException(e);
		}

		if (!"Success".equals(response)) {
			throw new SettingsException("Error in transformer: " + response);
		}
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
}
