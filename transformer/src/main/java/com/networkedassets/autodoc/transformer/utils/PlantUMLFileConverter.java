package com.networkedassets.autodoc.transformer.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.common.base.Preconditions;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class PlantUMLFileConverter implements HtmlFileConventer {
	private static final String encoding = "UTF-8";

	private String plantUmlDescription;
	private FileFormat fileformat;

	public PlantUMLFileConverter(String plantUmlDescription, FileFormat fileFormat) {
		this.plantUmlDescription = plantUmlDescription;
		this.fileformat = fileFormat;
	}

	@Override
	public String convert(String fileContent) {

		String results = "";
		Preconditions.checkNotNull(fileContent);
		Document doc = Jsoup.parse(fileContent);
		String packageName = doc.select("div.subTitle").first().text();
		String className = doc.select("div.subTitle").first().text();
		String plantUMLDescription = replaceClassDependency(
				String.format("%s.%s", packageName, className).replaceAll("\\s", ""));

		try {
			results = (fileformat != null) ? generateImage(plantUMLDescription) : plantUMLDescription;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return results;
	}

	public String getFileDescription(String fileContent) {

		Document doc = Jsoup.parse(fileContent);
		return doc.select("div.subTitle").first().text();

	}

	public String getFileName(String fileContent) {
		Document doc = Jsoup.parse(fileContent);
		return doc.select("h2.title").first().text();
	}

	private String replaceClassDependency(String fullClassName) {
		return Pattern.compile(System.getProperty("line.separator")).splitAsStream(this.plantUmlDescription).filter(s -> s.contains(fullClassName))
				.sorted().collect(Collectors.joining(System.getProperty("line.separator")));
	}

	private String generateImage(@Nonnull String plantUMLDescription) throws IOException {

		String svg = "";
		Preconditions.checkNotNull(plantUMLDescription);

		SourceStringReader reader = new SourceStringReader(plantUMLDescription);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();

		reader.generateImage(os, new FileFormatOption(fileformat));
		os.close();
		svg = new String(os.toByteArray(), Charset.forName(encoding));

		return svg;

	}

}
