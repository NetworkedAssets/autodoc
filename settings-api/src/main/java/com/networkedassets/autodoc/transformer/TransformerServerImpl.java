package com.networkedassets.autodoc.transformer;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.transformer.settings.SettingsException;
import com.networkedassets.autodoc.transformer.settings.SettingsForSpace;

import java.io.IOException;

public class TransformerServerImpl implements TransformerServer {
    public static final String SETTINGS = "/settings";

    private String url;
    private String confluenceUrl;

    public TransformerServerImpl(String url) {
        this.url = url;
        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();

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

    public TransformerServerImpl(String transformerUrl, String confluenceUrl) {
        this(transformerUrl);
        setConfluenceUrl(confluenceUrl);
    }

    @Override
    public SettingsForSpace getSettingsForSpace(String spaceKey) throws SettingsException {
        HttpResponse<SettingsForSpace> response;
        try {
            response = Unirest.get(url + SETTINGS)
                    .queryString("spaceKey", spaceKey)
                    .asObject(SettingsForSpace.class);
        } catch (UnirestException e) {
            SettingsException up = new SettingsException(e);
            throw up; // heh
        }

        return response.getBody();
    }

    @Override
    public void saveSettingsForSpace(SettingsForSpace settings, String spaceKey) throws SettingsException {
        HttpResponse<String> response;
        try {
            if (confluenceUrl != null) {
                response = Unirest.post(url + SETTINGS)
                        .queryString("spaceKey", spaceKey)
                        .queryString("confluenceUrl", confluenceUrl)
                        .body(settings)
                        .asString();
            } else {
                response = Unirest.post(url + SETTINGS)
                        .queryString("spaceKey", spaceKey)
                        .body(settings)
                        .asString();
            }
        } catch (UnirestException e) {
            throw new SettingsException(e);
        }

        if (!response.getBody().equals("Success")) {
            throw new SettingsException("Could not save settings: " + response.getBody());
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
