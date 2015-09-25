package com.networkedassets.autodoc.transformer.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities.EscapeMode;

public class CounfluenceFileConverter implements HtmlFileConventer {

	@Override
	public String convert(String fileContent) {
		// TODO
		return null;
	}

	private String getHeaderContent(final String fileContent) {

		Document doc = Jsoup.parse(fileContent);
		doc.outputSettings().escapeMode(EscapeMode.xhtml);
		return doc.select("div.header~*").first().html();
	}

	private String replaceLink(final String fileContent) {
		return fileContent;

	}

}
