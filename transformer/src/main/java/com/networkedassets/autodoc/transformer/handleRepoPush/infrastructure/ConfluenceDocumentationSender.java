package com.networkedassets.autodoc.transformer.handleRepoPush.infrastructure;

import com.google.common.base.Strings;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.handleRepoPush.DocumentationPiece;
import com.networkedassets.autodoc.transformer.handleRepoPush.require.DocumentationSender;
import com.networkedassets.autodoc.transformer.settings.Settings;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceDocumentationSender implements DocumentationSender {

	private static final String confluenceEndpointPostFormat =
			"/rest/doc/1.0/documentation/{project}/{repo}/{branch}/{docType}/{pieceName}";
	private static final String confluenceEndpointDeleteFormat =
			"/rest/doc/1.0/documentation/{project}/{repo}/{branch}/{docType}";

	private static final Logger log = LoggerFactory.getLogger(PushEventService.class);

	@Override
	public boolean send(Documentation documentation, Settings settings) {
		String confluenceUrl = settings.getConfluenceUrl();

		if (Strings.isNullOrEmpty(confluenceUrl)) {
			log.error("Confluence url isn't set!");
			return false;
		}
		confluenceUrl = confluenceUrl.endsWith("/") ? confluenceUrl.substring(0, confluenceUrl.length() - 1) : confluenceUrl;
		final String versionId = RandomStringUtils.randomAlphanumeric(20);

		postAllDocumentationPieces(documentation, confluenceUrl,
				settings.getCredentials().getConfluenceUsername(), settings.getCredentials().getConfluencePassword(), versionId);
		deleteAllRedundantDocumentationPieces(documentation, confluenceUrl,
				settings.getCredentials().getConfluenceUsername(), settings.getCredentials().getConfluencePassword(), versionId);
        return true;
	}

	private void postAllDocumentationPieces(Documentation documentation, String confluenceUrl, String username, String password, String versionId) {
		Escaper escaper = UrlEscapers.urlPathSegmentEscaper();

		for (DocumentationPiece docPiece : documentation.getPieces()) {
			try {
				final HttpRequestWithBody request = Unirest.post(confluenceUrl + confluenceEndpointPostFormat)
						.routeParam("project", escaper.escape(documentation.getProject()))
						.routeParam("repo", escaper.escape(documentation.getRepo()))
						.routeParam("branch", escaper.escape(documentation.getBranch()))
						.routeParam("docType", escaper.escape(documentation.getType().toString()))
						.routeParam("pieceName", escaper.escape(docPiece.getPieceName()))
						.basicAuth(username, password)
						.queryString("pieceType", docPiece.getPieceType())
						.queryString("versionId", versionId)
						.header("Content-Type", "application/json");

				log.info("Unirest POST TO URL: " + request.getUrl());
				log.info("Response: {}", request.body(docPiece.getContent()).asString().getBody());
			} catch (UnirestException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	private void deleteAllRedundantDocumentationPieces(Documentation documentation, String confluenceUrl, String username, String password, String versionId) {
		Escaper escaper = UrlEscapers.urlPathSegmentEscaper();
		try {
			final HttpRequestWithBody request = Unirest.delete(confluenceUrl + confluenceEndpointDeleteFormat)
					.routeParam("project", escaper.escape(documentation.getProject()))
					.routeParam("repo", escaper.escape(documentation.getRepo()))
					.routeParam("branch", escaper.escape(documentation.getBranch()))
					.routeParam("docType", escaper.escape(documentation.getType().toString()))
					.queryString("versionId", versionId)
					.basicAuth(username, password);

			log.info("Unirest DELETE TO URL: " + request.getUrl());
			log.info("Response: {}", request.asString().getBody());
		} catch (UnirestException exception) {
			throw new RuntimeException(exception);
		}
	}

}
