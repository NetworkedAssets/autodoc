package com.networkedassets.autodoc.transformer.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * Converts javaDoc files to Confluence storage format
 */

public class CounfluenceFileConverter implements HtmlFileConventer {

	private static final String outerLinkTemplate = "<ac:link><ri:page ri:content-title=\"%s\" /><ac:plain-text-link-body><![CDATA[%s]]></ac:plain-text-link</ac:link>";
	private static final String innerLinkTemplate = "<ac:link ac:anchor=\"%s\"><ac:plain-text-link-body><![CDATA[#%s]]></ac:plain-text-link-body></ac:link>";

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
	 * @param sufix
	 *            text adding to outer links
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
		replaceInnerLinks(doc);
		replaceOuterLinks(doc);
		replaceListTag(doc);
		return doc.select("div.header~*").first().html();
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

	private void replaceInnerLinks(@Nonnull Document doc) {
		Preconditions.checkNotNull(doc);
		Elements urls = doc.select("span.memberNameLink>a");

		urls.stream().forEach(url -> url.after(
				String.format(innerLinkTemplate, url.text().replaceAll("\\s", ""), url.text().replaceAll("\\s", "")))
				.remove());

	}

	private void replaceOuterLinks(@Nonnull Document doc) {
		Preconditions.checkNotNull(doc);
		String packageName = doc.select("div.subTitle").first().text().replaceAll("\\s", "");
		Elements urls = doc.select("a[href]");

		urls.stream().forEach(url -> url
				.after(String.format(outerLinkTemplate,
						String.format("%s.%s", packageName,
								!Strings.isNullOrEmpty(this.suffix) ? url.text().replaceAll("\\s", "") + this.suffix
										: url.text().replaceAll("\\s", "")),
						url.text().replaceAll("\\s", "")))
				.remove());

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
