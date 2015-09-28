package com.networkedassets.autodoc.transformer.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.select.Elements;

/**
 * Converts javaDoc files to Confluence storage format
 */

public class CounfluenceFileConverter implements HtmlFileConventer {

	private static final String outerLinkTemplate = "<ac:link><ri:page ri:content-title=\"%s\" /><ac:plain-text-link-body><![CDATA[%s]]></ac:plain-text-link</ac:link>";
	private static final String innerLinkTemplate = "<ac:link ac:anchor=\"%s\"><ac:plain-text-link-body><![CDATA[#%s]]></ac:plain-text-link-body></ac:link>";

	
	 /**
     * Converts javadoc html to a format usable with Atlassian Confluence
     * 
     * @param fileContent
     *            text representing html to be converted
     * @return  text javadoc page in Atlassian Confluence's storage format
     */
	
	
	@Override
	public String convert(String fileContent) {
		Document doc = Jsoup.parse(fileContent);
	    doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
	    doc.outputSettings().charset("UTF-8");
	    replaceInnerLinks(doc);
	    replaceOuterLinks(doc);
	    replaceListTag(doc);
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

	private static void replaceListTag(Document doc) {
		doc.select("tt").tagName("code");
		doc.select("dt").tagName("b").wrap("<p></p>");
		doc.select("dd").tagName("code").wrap("<p></p>");
		doc.select("dl").unwrap();
		doc.select(".blockListLast").removeClass("blockListLast").addClass("blockList");
		doc.select("li.blocklist").attr("style", "list-style:none;");

		// collapse inheritance tree to a sane <ul>
		for (final Element ul : doc.select("ul.inheritance")) {
			final Elements children = ul.children();
			final Element il = ul.appendElement("li");
			children.remove();
			il.insertChildren(0, children);
			children.unwrap();
		}

	}

}
