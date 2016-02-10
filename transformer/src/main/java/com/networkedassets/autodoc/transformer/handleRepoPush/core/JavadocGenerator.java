package com.networkedassets.autodoc.transformer.handleRepoPush.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.markusbernhardt.xmldoclet.xjc.*;
import com.github.markusbernhardt.xmldoclet.xjc.Class;
import com.github.markusbernhardt.xmldoclet.xjc.Enum;
import com.github.markusbernhardt.xmldoclet.xjc.Package;
import com.networkedassets.autodoc.transformer.handleRepoPush.Code;
import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.handleRepoPush.DocumentationPiece;
import com.networkedassets.autodoc.transformer.util.javadoc.Javadoc;
import com.networkedassets.autodoc.transformer.util.javadoc.JavadocException;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class JavadocGenerator implements DocumentationGenerator {

	@SuppressWarnings("unused")
    private static Logger log = LoggerFactory.getLogger(JavadocGenerator.class);

    // TODO: resolve the problem of other documentation generators depending on partial results from different generators
    private static Root cachedRoot;

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static Root getCachedRoot() {
        return cachedRoot;
    }

    @Override
	public Documentation generateFrom(Code code) {
		try {
			Root docRoot = Javadoc.structureFromDirectory(code.getCodePath());
            cachedRoot = docRoot;
			return convert(docRoot);
		} catch (JavadocException | JSONException | JsonProcessingException e) {
			throw new RuntimeException("Couldn't generate Javadoc", e);
		}
	}

	public static String jaxbToJson(Object val) throws JsonProcessingException {
		return OBJECT_MAPPER.writeValueAsString(val);
	}

	private static Documentation convert(Root docRoot) throws JsonProcessingException {
		Documentation doc = new Documentation(new ArrayList<>());
		doc.setType(DocumentationType.JAVADOC);

		List<DocumentationPiece> pieces = doc.getPieces();
		String index = jaxbToJson(docRoot.getIndex());
		pieces.add(new DocumentationPiece("index", "index", index));

		for (Package p : docRoot.getPackage()) {
			for (Enum e : p.getEnum()) {
				String en = jaxbToJson(e);
				pieces.add(new DocumentationPiece(e.getQualified(), "enum", en));
			}
			for (Interface i : p.getInterface()) {
				String in = jaxbToJson(i);
				pieces.add(new DocumentationPiece(i.getQualified(), "interface", in));
			}
			for (Class c : p.getClazz()) {
				String cl = jaxbToJson(c);
				pieces.add(new DocumentationPiece(c.getQualified(), "class", cl));
			}
            for (Annotation a : p.getAnnotation()) {
                String an = jaxbToJson(a);
                pieces.add(new DocumentationPiece(a.getQualified(), "annotation", an));
            }
		}

		return doc;
	}
}
