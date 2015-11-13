package com.networkedassets.autodoc.transformer.handleRepoPush.infrastructure;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.handleRepoPush.require.DocumentationSender;
import com.networkedassets.autodoc.transformer.settings.SettingsForSpace;

import java.util.Collection;

// TODO: go back to this class, when Confluence side of things is implemented
public class ConfluenceDocumentationSender implements DocumentationSender {
    private static final String confluenceEndpoint = "/doc";
    private String username = "mrobakowski";
    private String password = "admin";

    @Override
    public void send(Documentation documentation, Collection<SettingsForSpace> interestedSpaces) {
        if (interestedSpaces.isEmpty()) return;
        String confluenceUrl = interestedSpaces.stream().findAny().map(SettingsForSpace::getConfluenceUrl).get();
        try {
            Unirest.post(confluenceUrl + confluenceEndpoint).basicAuth(username, password)
                    .body(documentation).asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
