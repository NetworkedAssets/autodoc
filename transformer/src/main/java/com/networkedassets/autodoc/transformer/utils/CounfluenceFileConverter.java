package com.networkedassets.autodoc.transformer.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.select.Elements;

/**
 * Converts javaDoc files to Confluence storage format
 */

public class CounfluenceFileConverter implements HtmlFileConventer {

	private static final String outerLinkTemplate = "<ac:link><ri:page ri:content-title=\"%s\" /><ac:plain-text-link-body><![CDATA[%s]]></ac:plain-text-link</ac:link>";
	private static final String innerLinkTemplate = "<ac:link ac:anchor=\"%s\"><ac:plain-text-link-body><![CDATA[#%s]]></ac:plain-text-link-body></ac:link>";

	@Override
	public String convert(String fileContent) {
		Document doc = Jsoup.parse(fileContent);
		doc.outputSettings().escapeMode(EscapeMode.xhtml);
		replaceInnerLinks(doc);
		replaceOuterLinks(doc);
		return doc.select("div.header~*").first().html();
	}

	private static void replaceInnerLinks(Document doc) {

		Elements urls = doc.select("span.memberNameLink>a");

		urls.stream().forEach(url -> url.after(String.format(innerLinkTemplate, url.text(), url.text())).remove());

	}

	private static void replaceOuterLinks(Document doc) {

		Elements urls = doc.select("a[href]");

		urls.stream().forEach(url -> url.after(String.format(outerLinkTemplate, url.text(), url.text())).remove());

	}

}
