package com.networkedassets.autodoc.transformer.util.uml;

import com.fasterxml.jackson.core.JsonParseException;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by mtulaza on 2016-02-05.
 */
public class UmlJsonDocumentationParserTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Test
    public void expectedJsonParseException() throws IOException {
        expectedException.expect(JsonParseException.class);
        final String PARSING_ERROR_JSON = "{'name'ddd: 'sialaasdasdlala'sasas}";
        new UmlJsonDocumentationParser(PARSING_ERROR_JSON);
    }

    @Test
    public void testFilterAndComposeJSON() throws IOException {
        final String docPieceName = "com.networkedassets.autodoc.transformer.JavaDocGenerator";
        UmlJsonDocumentationParser parser = new UmlJsonDocumentationParser(getJSONfromFile("proper_json.json"));

        Optional<String> composedJSON = parser.filterAndComposeJSON(docPieceName);
        assertTrue(composedJSON.isPresent());
        assertFalse(composedJSON.get().isEmpty());
    }

    private String getJSONfromFile(final String filename) throws IOException {
        return IOUtils.toString(
                    getClass().getResourceAsStream(filename)
            );
    }
}
