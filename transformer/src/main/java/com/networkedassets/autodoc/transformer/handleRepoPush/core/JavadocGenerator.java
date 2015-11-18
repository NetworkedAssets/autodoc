package com.networkedassets.autodoc.transformer.handleRepoPush.core;

import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;

import java.io.IOException;
import java.nio.file.Path;

import org.json.JSONException;

import com.networkedassets.autodoc.transformer.handleRepoPush.Code;
import com.networkedassets.autodoc.transformer.util.javadoc.Javadoc;
import com.networkedassets.autodoc.transformer.util.javadoc.JavadocConverter;
import com.networkedassets.autodoc.transformer.util.javadoc.JavadocException;

/**
 * Created by mrobakowski on 11/13/2015.
 */
public class JavadocGenerator implements DocumentationGenerator {
	

	@Override
	public Documentation generateFrom(Code code) {
		try {
			Path xmlJavaDocPath = Javadoc.fromDirectory(code.getCodePath(), null, "com.jeldoclet.JELDoclet");

			return new JavadocConverter(xmlJavaDocPath).convert();

		} catch (JavadocException | JSONException | IOException e) {
			throw new RuntimeException("Couldn't generate Javadoc", e);
		}
	}
}
