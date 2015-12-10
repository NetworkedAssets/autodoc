package com.networkedassets.autodoc.transformer.handleRepoPush.core;

import com.github.markusbernhardt.xmldoclet.xjc.*;
import com.github.markusbernhardt.xmldoclet.xjc.Class;
import com.github.markusbernhardt.xmldoclet.xjc.Enum;
import com.github.markusbernhardt.xmldoclet.xjc.Package;
import com.networkedassets.autodoc.transformer.handleRepoPush.Code;
import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.handleRepoPush.DocumentationPiece;
import com.networkedassets.autodoc.transformer.util.javadoc.Javadoc;
import com.networkedassets.autodoc.transformer.util.javadoc.JavadocException;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavadocGenerator implements DocumentationGenerator {

	@SuppressWarnings("unused")
    private static Logger log = LoggerFactory.getLogger(JavadocGenerator.class);

    // TODO: resolve the problem of other documentation generators depending on partial results from different generators
    private static Root cachedRoot;

    public static Root getCachedRoot() {
        return cachedRoot;
    }

    @Override
	public Documentation generateFrom(Code code) {
		try {
			Root docRoot = Javadoc.structureFromDirectory(code.getCodePath());
            cachedRoot = docRoot;
			return convert(docRoot);
		} catch (JavadocException | JSONException | JAXBException e) {
			throw new RuntimeException("Couldn't generate Javadoc", e);
		}
	}

	public static String jaxbToJson(java.lang.Class<?> clazz, Object val) throws JAXBException {
		Map<String, Object> properties = new HashMap<>(2);
		properties.put(MarshallerProperties.MEDIA_TYPE, "application/json");
		properties.put(MarshallerProperties.JSON_INCLUDE_ROOT, true);
		properties.put(MarshallerProperties.INDENT_STRING, true);
		JAXBContext contextObj = JAXBContextFactory.createContext(new java.lang.Class[] { clazz, org.eclipse.persistence.jaxb.xmlmodel.ObjectFactory.class },
				properties);
		Marshaller marshaller = contextObj.createMarshaller();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		marshaller.marshal(val, baos);
		return baos.toString();
	}

	private static Documentation convert(Root docRoot) throws JAXBException {
		Documentation doc = new Documentation(new ArrayList<>());
		doc.setType(DocumentationType.JAVADOC);

		List<DocumentationPiece> pieces = doc.getPieces();
		String index = jaxbToJson(Index.class, docRoot.getIndex());
		pieces.add(new DocumentationPiece("index", "index", index));

		for (Package p : docRoot.getPackage()) {
			for (Enum e : p.getEnum()) {
				String en = jaxbToJson(Enum.class, e);
				pieces.add(new DocumentationPiece(e.getQualified(), "enum", en));
			}
			for (Interface i : p.getInterface()) {
				String in = jaxbToJson(Interface.class, i);
				pieces.add(new DocumentationPiece(i.getQualified(), "interface", in));
			}
			for (Class c : p.getClazz()) {
				String cl = jaxbToJson(Class.class, c);
				pieces.add(new DocumentationPiece(c.getQualified(), "class", cl));
			}
            for (Annotation a : p.getAnnotation()) {
                String an = jaxbToJson(Annotation.class, a);
                pieces.add(new DocumentationPiece(a.getQualified(), "annotation", an));
            }
		}

		return doc;
	}
}
