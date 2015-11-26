package com.networkedassets.autodoc.transformer.handleRepoPush.infrastructure;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.handleRepoPush.DocumentationPiece;
import com.networkedassets.autodoc.transformer.handleRepoPush.require.DocumentationSender;
import com.networkedassets.autodoc.transformer.settings.ConfluenceSettings;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

// TODO: go back to this class, when Confluence side of things is implemented
public class ConfluenceDocumentationSender implements DocumentationSender {
    private static final String confluenceEndpointFormat = "%s/rest/autodoc/1.0/documentation/%s/%s/%s/%s";
    private String username = "mrobakowski";
    private String password = "admin";

    @Override
    public void send(Documentation documentation, ConfluenceSettings confluenceSettings) {

        String url = confluenceSettings.getConfluenceUrl();
        url = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
        for (DocumentationPiece docPiece : documentation.getPieces()) {
            try {
                System.out.println(Unirest.post(
                        String.format(confluenceEndpointFormat, url,
                                documentation.getProject(),
                                documentation.getRepo(),
                                documentation.getBranch(),
                                docPiece.getPieceName()))
                        .basicAuth(username, password)
                        .queryString("docType", documentation.getType())
                        .queryString("pieceType", docPiece.getPieceType())
                        .header("Content-Type", "application/json")
                        .body(docPiece.getContent())
                        .asString().getBody());
            } catch (UnirestException e) {
                throw new RuntimeException(e);
            }
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
