package com.networkedassets.autodoc.jsondoclet;

import com.networkedassets.autodoc.jsondoclet.model.Root;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonDoclet {

	private final static Logger log = LoggerFactory.getLogger(Parser.class);

	/**
	 * The parsed object model.
	 */
	public static Root root;

	public static boolean start(RootDoc rootDoc) {
		Parser parser = new Parser();
		root = parser.parseRootDoc(rootDoc);
		return true;
	}

	public static LanguageVersion languageVersion() {
		return LanguageVersion.JAVA_1_5;
	}
}
