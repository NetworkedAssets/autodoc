package com.networkedassets.autodoc.transformer;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.IOUtils;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.transformer.settings.Branch;
import com.networkedassets.autodoc.transformer.settings.Settings;
import com.networkedassets.autodoc.transformer.settings.SettingsException;
import com.networkedassets.autodoc.transformer.settings.Source;

public class TransformerClient {
    private static final String SETTINGS = "/settings";
    private static final String CREDENTIALS = "/settings/credentials";
    private static final String SOURCES = "/sources/";
    private static final String EXTENDED_SOURCES = "/sources/extended/";
    private static final String EVENT = "/event";
    private static final String EVENT_JSON = "{\"sourceUrl\":\"%s\",\"projectKey\":\"%s\",\"repositorySlug\":\"%s\",\"branchId\":\"%s\"}";

    public static final Logger log = LoggerFactory.getLogger(TransformerClient.class);

    private String url;

    public TransformerClient(String url) {
        log.debug("Transformer server constructing");
        this.url = url;
        Unirest.setObjectMapper(getConfiguredObjectMapper());
        Unirest.setHttpClient(getConfiguredHttpClient());
    }

    public HttpResponse<String> getSettings() throws SettingsException {
        try {
            return Unirest.get(url + SETTINGS).asString();
        } catch (UnirestException e) {
            throw new SettingsException(e);
        }
    }

    public HttpResponse<String> getCredentials() throws SettingsException {
        try {
            Unirest.get(url).asString();
            return Unirest.get(url + CREDENTIALS).header("Content-Type", "application/json").asString();
        } catch (UnirestException e) {
            throw new SettingsException(e);
        }
    }

    public HttpResponse<String> setCredentials(Settings settings) throws SettingsException {
        try {
            return Unirest.post(url + CREDENTIALS).header("Content-Type", "application/json").body(settings)
                    .asString();
        } catch (UnirestException e) {
            throw new SettingsException(e);
        }
    }

    public HttpResponse<String> modifyBranch(int sourceId, String projectKey, String repoSlug, String branchId, Branch branch)
            throws SettingsException {
        try {
            return Unirest
                    .put(url + "/sources/{sourceId}/{projectKey}/{repoSlug}/{branchId}")
                    .routeParam("sourceId", Integer.toString(sourceId))
                    .routeParam("projectKey", URLEncoder.encode(projectKey, "UTF-8"))
                    .routeParam("repoSlug", URLEncoder.encode(repoSlug, "UTF-8"))
                    .routeParam("branchId", URLEncoder.encode(branchId, "UTF-8"))
                    .header("Content-Type", "application/json").body(branch).asString();
        } catch (UnirestException | IOException e) {
            throw new SettingsException(e);
        }
    }

    public HttpResponse<String> getSources() throws SettingsException {
        try {
            return Unirest.get(url + SOURCES).asString();
        } catch (UnirestException e) {
            throw new SettingsException(e);
        }
    }

    public HttpResponse<String> getExtendedSources() throws SettingsException {
        try {
            return Unirest.get(url + EXTENDED_SOURCES).asString();
        } catch (UnirestException e) {
            throw new SettingsException(e);
        }
    }

    public HttpResponse<String> forceGenerate(String sourceUrl, String projectKey, String repoSlug, String branchId)
            throws SettingsException {
        String eventPayload = String.format(EVENT_JSON, sourceUrl, projectKey, repoSlug, branchId);
        try {
            return Unirest.post(url + EVENT).header("Content-Type", "application/json").body(eventPayload)
                    .asString();
        } catch (UnirestException e) {
            throw new SettingsException(e);
        }
    }

    public HttpResponse<String> getSource(String id) throws SettingsException {
        try {
            return Unirest.get(url + SOURCES + "{id}").routeParam("id", id).asString();
        } catch (UnirestException e) {
            throw new SettingsException(e);
        }
    }

    public HttpResponse<String> setSource(Source source) throws SettingsException {
        try {
            return Unirest.post(url + SOURCES).header("Content-Type", "application/json").body(source)
                    .asString();
        } catch (UnirestException e) {
            throw new SettingsException(e);
        }
    }

    public HttpResponse<Source> changeSource(int sourceId, Source source) throws SettingsException {
        try {

            return Unirest.put(url + SOURCES + String.valueOf(sourceId)).header("Content-Type", "application/json")
                    .body(source).asObject(Source.class);
        } catch (UnirestException e) {
            throw new SettingsException(e);
        }
    }

    public HttpResponse<String> removeSource(int sourceId) throws SettingsException {
        try {
            return Unirest.delete(url + SOURCES + String.valueOf(sourceId)).asString();
        } catch (UnirestException e) {
            throw new SettingsException(e);
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private static CloseableHttpClient getConfiguredHttpClient() {
        try {
            return HttpClients.custom().setHostnameVerifier(new AllowAllHostnameVerifier())
                    .setSslcontext(new SSLContextBuilder().loadTrustMaterial(null, (_1, _2) -> true).build()).build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    private static ObjectMapper getConfiguredObjectMapper() {

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
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

    }
}
