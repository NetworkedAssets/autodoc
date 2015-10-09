package com.networkedassets.autodoc.transformer.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class PlantUMLFileConverter implements HtmlFileConventer {
	private static final String encoding = "UTF-8";
	private static final String newline = System.getProperty("line.separator");
	private static final String plantUmlTemplate = "<ac:structured-macro ac:name=\"plantuml\"><ac:plain-text-body><![CDATA[%s%s%s]]></ac:plain-text-body></ac:structured-macro>";

	private String plantUmlDescription;
	private FileFormat fileformat;
	private String suffix;

	/**
	 * Main constructor
	 *
	 * @param plantUmlDescription
	 *            plant Uml description
	 * @param suffix
	 *            added to plant uml confluence link
	 * @param fileFormat
	 *            file format for uml diagram based on plant uml description
	 */

	public PlantUMLFileConverter(String plantUmlDescription, List<String> listClassName, String suffix, FileFormat fileFormat) {
		this.plantUmlDescription = plantUmlDescription;
		this.fileformat = fileFormat;
		this.suffix = suffix;
	}

	/**
	 * Converts javadoc html to a format usable with Plant UML Atlassian
	 * Confluence Plugin
	 * 
	 * @param fileContent
	 *            html representing one javadoc class
	 * 
	 * @return text javadoc page in Plant UML Atlassian Confluence's format
	 * 
	 */

	@Override
	public String convert(String fileContent) {

		Preconditions.checkNotNull(fileContent);

		Document doc = Jsoup.parse(fileContent);
		String packageName = doc.select("div.subTitle").first().text();
		String className = doc.select("title").first().text();
		String plantUMLDescription = replaceClassDependency(
				String.format("%s.%s", packageName, className).replaceAll("\\s", ""));

		return !Strings.isNullOrEmpty(plantUmlDescription)
				? String.format(plantUmlTemplate, newline, plantUMLDescription, newline) : "";
	}

	/**
	 * Return description of html file. For javaDoc html file return package
	 * name.
	 * 
	 * @param fileContent
	 *            html representing the one javaDoc class
	 * 
	 * @return package name
	 * 
	 */

	public String getFileDescription(String fileContent) {

		Document doc = Jsoup.parse(fileContent);
		return doc.select("div.subTitle").first().text();

	}

	/**
	 * Return fileName of html file. For javaDoc html file return class name.
	 * 
	 * @param fileContent
	 *            html representing the one javaDoc class
	 * 
	 * @return class name
	 * 
	 */

	public String getFileName(String fileContent) {
		Document doc = Jsoup.parse(fileContent);
		return doc.select("h2.title").first().text();
	}

	private String replaceClassDependency(String fullClassName) {
		return Pattern.compile(newline).splitAsStream(this.plantUmlDescription).filter(s -> s.contains(fullClassName))
				.sorted().collect(Collectors.joining(newline));
	}

	private String addConfluenceLinks(String suffix) {
		return null;

	}

	@SuppressWarnings("unused")
	private String generateImage(@Nonnull String plantUMLDescription) throws IOException {

		Preconditions.checkNotNull(plantUMLDescription);

		SourceStringReader reader = new SourceStringReader(plantUMLDescription);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();

		reader.generateImage(os, new FileFormatOption(fileformat));
		os.close();
		return new String(os.toByteArray(), Charset.forName(encoding));

	}

}
