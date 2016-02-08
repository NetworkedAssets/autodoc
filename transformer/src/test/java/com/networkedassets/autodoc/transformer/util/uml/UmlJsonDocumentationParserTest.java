package com.networkedassets.autodoc.transformer.util.uml;

import com.fasterxml.jackson.core.JsonParseException;
import com.networkedassets.autodoc.transformer.util.uml.data.Relation;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by mtulaza on 2016-02-05.
 */
public class UmlJsonDocumentationParserTest {

    @Test(expected = JsonParseException.class)
    public void expectedJsonParseException() throws IOException {
        String PARSING_ERROR_JSON = "{'name'ddd: 'sialaasdasdlala'sasas}";
        new UmlJsonDocumentationParser(PARSING_ERROR_JSON);
    }

    @Test
    public void testFindAllRelationsGivenProperJSONreturnsNotEmptySet() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        UmlJsonDocumentationParser parser = new UmlJsonDocumentationParser(getJSONfromFile("proper_json.json"));
        Object findAllRelationsInvokeResult = giveMePrivateMethod(UmlJsonDocumentationParser.class, "findAllRelations").invoke(parser, null);

        assertTrue(findAllRelationsInvokeResult instanceof Set);
        Set<String> invokeResultSet = (Set<String>) findAllRelationsInvokeResult;
        assertFalse(invokeResultSet.isEmpty());
    }

    /*
     * "relations" node was not found in findAllRelations method
     */
    @Test(expected = Exception.class)
    public void testFindAllRelationsGivenImproperJSONshouldThrowException() throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        UmlJsonDocumentationParser parser = new UmlJsonDocumentationParser(getJSONfromFile("improper_json.json"));
        giveMePrivateMethod(UmlJsonDocumentationParser.class, "findAllRelations").invoke(parser, null);
    }

    @Test
    public void testFindRelationsByReturnsNotEmptySetForProperJSON() throws NoSuchMethodException, IOException, InvocationTargetException, IllegalAccessException {
        Method findRelationsByMethod = giveMePrivateMethod(UmlJsonDocumentationParser.class, "findRelationsBy", String.class);
        UmlJsonDocumentationParser parser = new UmlJsonDocumentationParser(getJSONfromFile("proper_json.json"));
        final String docPieceName = "com.networkedassets.autodoc.transformer.JavaDocGenerator";

        Object invokeResult = findRelationsByMethod.invoke(parser, docPieceName);
        assertTrue(invokeResult instanceof Set);
        Set<Relation> relations = (Set<Relation>) invokeResult;
        assertFalse(relations.isEmpty());
    }

    @Test
    public void testFilterAndComposeJSON() throws IOException {
        final String docPieceName = "com.networkedassets.autodoc.transformer.JavaDocGenerator";
        UmlJsonDocumentationParser parser = new UmlJsonDocumentationParser(getJSONfromFile("proper_json.json"));

        Optional<String> composedJSON = parser.filterAndComposeJSON(docPieceName);
        assertTrue(composedJSON.isPresent());
        assertFalse(composedJSON.get().isEmpty());
    }

    private Method giveMePrivateMethod(Class<UmlJsonDocumentationParser> fromClass, final String methodName, @Nullable Class... args) throws NoSuchMethodException {
        Method method = fromClass.getDeclaredMethod(methodName, args);
        method.setAccessible(true);
        return method;
    }

    private String getJSONfromFile(final String filename) throws IOException {
        return IOUtils.toString(
                    getClass().getResourceAsStream(filename)
            );
    }
}
