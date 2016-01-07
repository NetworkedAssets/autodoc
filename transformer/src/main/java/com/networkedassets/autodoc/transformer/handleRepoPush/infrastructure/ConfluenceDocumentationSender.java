package com.networkedassets.autodoc.transformer.handleRepoPush.infrastructure;

import com.google.common.base.Strings;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.handleRepoPush.DocumentationPiece;
import com.networkedassets.autodoc.transformer.handleRepoPush.require.DocumentationSender;
import com.networkedassets.autodoc.transformer.settings.Settings;

public class ConfluenceDocumentationSender implements DocumentationSender {
    private static final String confluenceEndpointFormat = "%s/rest/autodoc/1.0/documentation/%s/%s/%s/%s/%s";
   
    @Override
    public void send(Documentation documentation, Settings settings) {
        String url = settings.getConfluenceUrl();
        if (Strings.isNullOrEmpty(url)) return;
        url = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
        for (DocumentationPiece docPiece : documentation.getPieces()) {
            try {
                Escaper e = UrlEscapers.urlPathSegmentEscaper();
                System.out.println(Unirest.post(
                        String.format(confluenceEndpointFormat, url,
                                e.escape(documentation.getProject()),
                                e.escape(documentation.getRepo()),
                                e.escape(documentation.getBranch()),
                                e.escape(documentation.getType().toString()),
                                e.escape(docPiece.getPieceName())))
                        .basicAuth(settings.getConfluenceUsername(), settings.getConfluencePassword())
                        .queryString("pieceType", docPiece.getPieceType())
                        .header("Content-Type", "application/json")
                        .body(docPiece.getContent())
                        .asString().getBody());
            } catch (UnirestException e) {
                throw new RuntimeException(e);
            }
        }
    }

   
}
