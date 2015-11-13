package com.networkedassets.autodoc.transformer.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Converts javaDoc files to Confluence storage format
 */

public class CounfluenceFileConverter implements HtmlFileConventer {

	private static final String linkTemplate = "<ac:link><ri:page ri:content-title=\"%s\" /><ac:plain-text-link-body><#$#![CDATA[%s]]#$#></ac:plain-text-link</ac:link>";
	private static final String anchorInnerTemplate = "<ac:link ac:anchor=\"%s\"><ac:plain-text-link-body><#$#![CDATA[%s]]#$#></ac:plain-text-link-body></ac:link>";
	private static final String anchorOuterTemplate = "<ac:link ac:anchor=\"%s\"><ri:page ri:content-title=\"%s\"/><ac:plain-text-link-body><#$#![CDATA[%s]]#$#></ac:plain-text-link-body></ac:link>";
	private String suffix;

	/**
	 * Main constructor
	 *
	 * @param suffix
	 *            suffix which will be added to confluence page
	 */

	public CounfluenceFileConverter(@Nullable String suffix) {

		this.suffix = suffix;

	}

	/**
	 * Converts javadoc html to a format usable with Atlassian Confluence
	 * 
	 * @param fileContent
	 *            text representing html to be converted
	 * 
	 * @return text javadoc page in Atlassian Confluence's storage format
	 * 
	 */

	@Override
	public String convert(@Nonnull String fileContent) {
		Preconditions.checkNotNull(fileContent);
		Document doc = Jsoup.parse(fileContent);
		doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
		doc.outputSettings().charset("UTF-8");
		replaceLinks(doc);
		replaceListTag(doc);
		// jsoup remove !CDATA
		return doc.select("div.header~*").first().html().replace("&lt;#$#", "<").replace("#$#&gt;", ">");
	}

	/**
	 * Return package name from javaDoc html
	 * 
	 * @param fileContent
	 *            text representing html javadoc to be converted
	 * @return package name
	 */

	public String getFileDescription(@Nonnull String fileContent) {
		Preconditions.checkNotNull(fileContent);
		Document doc = Jsoup.parse(fileContent);
		return doc.select("div.subTitle").first().text().replaceAll("\\s", "");

	}

	/**
	 * Return class name from javaDoc html
	 * 
	 * @param fileContent
	 *            text representing html javadoc to be converted
	 * @return class name
	 */

	public String getFileName(@Nonnull String fileContent) {
		Preconditions.checkNotNull(fileContent);
		Document doc = Jsoup.parse(fileContent);
		return doc.select("title").first().text().replaceAll("\\s", "");
	}

	public void setSuffix(String sufix) {
		this.suffix = sufix;
	}

	public String getSuffix() {
		return suffix;
	}

	private void replaceLinks(@Nonnull Document doc) {

		Preconditions.checkNotNull(doc);
		String className = doc.select("title").first().text().replaceAll("\\s", "");
		Elements urls = doc.select("a[href]");

		urls.stream().forEach(url -> url.after(createConfluenceLink(url.attr("href"), className)).remove());

	}

	private String createConfluenceLink(String href, String className) {

		String link = "";

		if (href.contains("#")) {
			if (getClassName(href).equals(className)) {

				link = String.format(anchorInnerTemplate, getMethodFieldName(href), getMethodFieldName(href));

			} else {

				link = !getPackageName(href).equals(getClassName(href))
						? String.format(anchorOuterTemplate, getMethodFieldName(href),
								!Strings.isNullOrEmpty(this.suffix) ? getPackageName(href) + this.suffix
										: getPackageName(href),
								getMethodFieldName(href))
						: String.format(anchorOuterTemplate,
								getMethodFieldName(href), !Strings.isNullOrEmpty(this.suffix)
										? getMethodFieldName(href) + this.suffix : getMethodFieldName(href),
								getMethodFieldName(href));
			}
		} else {

			link = String.format(linkTemplate,
					!Strings.isNullOrEmpty(this.suffix) ? getPackageName(href) + this.suffix : getPackageName(href),
					getClassName(href));
		}

		return link;
	}

	private String getPackageName(String href) {

		return href.contains(".html")
				? href.substring(0, href.lastIndexOf(".html")).replace("../", "").replace("/", ".") : href;

	}

	private String getClassName(String href) {

		return href.contains(".html") ? href.substring(href.lastIndexOf("/") + 1, href.lastIndexOf(".html")) : href;

	}

	private String getMethodFieldName(String href) {

		System.out.println(href);

		String result = href.contains("#") ? href.substring(href.indexOf("#") + 1, href.length()) : href;

		return result.contains("-") ? result.substring(0, result.indexOf("-")) : result;

	}

	private void replaceListTag(@Nonnull Document doc) {
		Preconditions.checkNotNull(doc);
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
