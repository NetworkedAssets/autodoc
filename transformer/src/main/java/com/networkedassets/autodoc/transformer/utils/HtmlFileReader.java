package com.networkedassets.autodoc.transformer.utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.networkedassets.autodoc.transformer.utils.data.HtmlFile;
import com.networkedassets.util.functional.Throwing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Read only javaDoc file with class description
 */

public class HtmlFileReader {

    private static Logger log = LoggerFactory.getLogger(HtmlFileReader.class);

    /**
     * Attention: the returned stream should be closed when you're done with it
     */
    public static Stream<HtmlFile> read(@Nonnull final Path path, @Nonnull final HtmlFileConventer converter) throws IOException {

        Preconditions.checkNotNull(path);
        Preconditions.checkNotNull(converter);

        return Files.walk(path, FileVisitOption.FOLLOW_LINKS).parallel()
                .filter(p -> !Files.isDirectory(p) &&
                        !p.getFileName().toString().contains("-") &&
                        !p.getFileName().toString().equals("index.html") &&
                        p.endsWith(".html"))
                .map(Throwing.rethrowAsRuntimeException(
                        p -> getTextContent(p, converter))
                );

    }

    private static HtmlFile getTextContent(@Nonnull final Path file, @Nonnull final HtmlFileConventer converter) throws IOException {

        Preconditions.checkNotNull(file);
        Preconditions.checkNotNull(converter);

        HtmlFile htmlFile = new HtmlFile(file.getFileName().toString(), file.toAbsolutePath().toString());

        String content = new String(Files.readAllBytes(file), Charset.defaultCharset());
        htmlFile.setAdditionalProperties(ImmutableMap.of(
                "packageName", converter.getFileDescription(content),
                "className", converter.getFileName(content)
        ));
        htmlFile.setFileContent(converter.convert(content));
        return htmlFile;
    }

}
