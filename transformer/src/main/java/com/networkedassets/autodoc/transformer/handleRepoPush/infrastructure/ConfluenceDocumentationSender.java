package com.networkedassets.autodoc.transformer.handleRepoPush.infrastructure;

import com.google.common.base.Strings;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.handleRepoPush.DocumentationPiece;
import com.networkedassets.autodoc.transformer.handleRepoPush.require.DocumentationSender;
import com.networkedassets.autodoc.transformer.settings.Settings;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ConfluenceDocumentationSender implements DocumentationSender {
    private static final String confluenceEndpointFormat = "%s/rest/autodoc/1.0/documentation/%s/%s/%s/%s/%s";
   
    @Override
    public void send(Documentation documentation, Settings settings) {
        String url = settings.getConfluenceUrl();
        if (Strings.isNullOrEmpty(url)) return;
        url = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
        for (DocumentationPiece docPiece : documentation.getPieces()) {
            try {
                // TODO: fix the bug where for some reason, even though it is escaped, branches in the form of refs/heads/master do not work
                System.out.println(Unirest.post(
                        String.format(confluenceEndpointFormat, url,
                                URLEncoder.encode(documentation.getProject(), "UTF-8"),
                                URLEncoder.encode(documentation.getRepo(), "UTF-8"),
                                URLEncoder.encode(documentation.getBranch(), "UTF-8"),
                                URLEncoder.encode(documentation.getType().toString(), "UTF-8"),
                                URLEncoder.encode(docPiece.getPieceName(), "UTF-8")))
                        .basicAuth(settings.getConfluenceUsername(), settings.getConfluencePassword())
                        .queryString("pieceType", docPiece.getPieceType())
                        .header("Content-Type", "application/json")
                        .body(docPiece.getContent())
                        .asString().getBody());
            } catch (UnirestException e) {
                throw new RuntimeException(e);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

   
}
