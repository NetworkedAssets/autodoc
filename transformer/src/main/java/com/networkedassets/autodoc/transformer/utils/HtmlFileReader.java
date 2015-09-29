package com.networkedassets.autodoc.transformer.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.networkedassets.autodoc.transformer.utils.data.HtmlFile;

/**
 * Read only javaDoc file with class description
 */

public class HtmlFileReader {

	private static Logger log = LoggerFactory.getLogger(HtmlFileReader.class);

	public static List<HtmlFile> read(final String path, final String extension, final HtmlFileConventer converter) {
		final List<HtmlFile> pages = new ArrayList<>();
		try (

		final Stream<Path> pathStream = Files.walk(Paths.get(path), Integer.MAX_VALUE, FileVisitOption.FOLLOW_LINKS)) {
			pathStream.parallel()

			.filter((p) -> !p.toFile().isDirectory() && !p.toFile().getName().contains("-")
					&& !p.toFile().getName().equals("index.html") && p.toFile().getAbsolutePath().endsWith(extension))
					.forEach(p -> getTextContent(p, pages, converter));
		} catch (final IOException e) {
			log.error("General I/O exception:", e);
		}
		return pages;
	}

	private static void getTextContent(final Path file, final List<HtmlFile> pages, final HtmlFileConventer converter) {

		HtmlFile htmlFile = new HtmlFile(file.toFile().getName(), file.toFile().getAbsolutePath());

		try {
			String content = new String(Files.readAllBytes(file), Charset.defaultCharset());
			htmlFile.setAdditionalProperties(Maps.newHashMap(ImmutableMap.of("packageName",
					converter.getFileDescription(content), "className", converter.getFileName(content))));
			htmlFile.setFileContent(converter.convert(content));
			pages.add(htmlFile);
		} catch (final IOException e) {
			log.error("General I/O exception:", e);
		}
	}

}
