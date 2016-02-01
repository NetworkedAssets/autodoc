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
    public static final String SETTINGS = "/settings";
    public static final String CREDENTIALS = "/settings/credentials";
    public static final String SOURCES = "/sources/";
    public static final String EXTENDED_SOURCES = "/sources/extended/";
    public static final String EVENT = "/event";
    public static final String EVENT_JSON = "{\"sourceUrl\":\"%s\",\"projectKey\":\"%s\",\"repositorySlug\":\"%s\",\"branchId\":\"%s\"}";

    public static final Logger log = LoggerFactory.getLogger(TransformerClient.class);

    private String url;

    public TransformerClient(String url) {
        log.debug("Transformer server constructing");
        this.url = url;
        Unirest.setObjectMapper(getConfiguredObjectMapper());
        Unirest.setHttpClient(getConfiguredHttpClient());
    }


    public HttpResponse<String> getSettings() throws SettingsException {
        HttpResponse<String> response;
        try {

            response = Unirest.get(url + SETTINGS).asString();
        } catch (UnirestException e) {
            throw new SettingsException(e);
        }

        return response;
    }

    public HttpResponse<String> getCredentials() throws SettingsException {
        HttpResponse<String> response;
        try {
            response = Unirest.get(url + CREDENTIALS).header("Content-Type", "application/json").asString();
        } catch (UnirestException e) {
            throw new SettingsException(e);
        }
        return response;
    }

    public HttpResponse<String> setCredentials(Settings settings) throws SettingsException {
        HttpResponse<String> response;
        try {
            response = Unirest.post(url + CREDENTIALS).header("Content-Type", "application/json").body(settings)
                    .asString();
        } catch (UnirestException e) {
            throw new SettingsException(e);
        }
        return response;
    }

    public Branch modifyBranch(int sourceId, String projectKey, String repoSlug, String branhId, Branch branch)
            throws SettingsException {
        try {
            HttpResponse<Branch> branchHttpResponse = Unirest
                    .put(url + "/sources/{sourceId}/{projectKey}/{repoSlug}/{branchId}")
                    .routeParam("sourceId", Integer.toString(sourceId))
                    .routeParam("projectKey", URLEncoder.encode(projectKey, "UTF-8"))
                    .routeParam("repoSlug", URLEncoder.encode(repoSlug, "UTF-8"))
                    .routeParam("branchId", URLEncoder.encode(branhId, "UTF-8"))
                    .header("Content-Type", "application/json").body(branch).asObject(Branch.class);
            if (branchHttpResponse.getStatus() == 200) {
                return branchHttpResponse.getBody();
            } else {
                throw new SettingsException(
                        "Could not modify branch: " + IOUtils.toString(branchHttpResponse.getRawBody()));
            }
        } catch (UnirestException | IOException e) {
            throw new SettingsException(e);
        }
    }

    public HttpResponse<String> getSources() throws SettingsException {
        HttpResponse<String> response;
        try {
            response = Unirest.get(url + SOURCES).asString();
        } catch (UnirestException e) {
            throw new SettingsException(e);
        }

        return response;
    }

    public HttpResponse<String> getExtendedSources() throws SettingsException {
        HttpResponse<String> response;
        try {
            response = Unirest.get(url + EXTENDED_SOURCES).asString();
        } catch (UnirestException e) {
            throw new SettingsException(e);
        }

        return response;
    }


    public HttpResponse<String> forceGenerate(String sourceUrl, String projectKey, String repoSlug, String branchId)
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
