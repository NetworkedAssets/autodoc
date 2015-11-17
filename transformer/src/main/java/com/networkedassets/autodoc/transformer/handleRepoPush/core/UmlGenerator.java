package com.networkedassets.autodoc.transformer.handleRepoPush.core;

import java.nio.file.Path;

import com.networkedassets.autodoc.transformer.handleRepoPush.Code;
import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.util.UmlClassDiagramConverter;
import com.networkedassets.autodoc.transformer.util.javadoc.Javadoc;
import com.networkedassets.autodoc.transformer.util.javadoc.JavadocException;
import com.networkedassets.autodoc.transformer.util.uml.PlantUML;
import com.networkedassets.autodoc.transformer.util.uml.PlantUMLException;

/**
 * Created by mrobakowski on 11/13/2015.
 */
public class UmlGenerator implements DocumentationGenerator {

	@Override
	public Documentation generateFrom(Code code) {
		try {

			Path plantUmlPath = PlantUML.fromDirectory(code.getCodePath(), null, null);
			Path xmlJavaDocPath=Javadoc.fromDirectory(code.getCodePath(), null, "com.jeldoclet.JELDoclet");
			return  new UmlClassDiagramConverter(xmlJavaDocPath,plantUmlPath).convert();
		} catch (PlantUMLException | JavadocException e) {
			throw new RuntimeException("Couldn't generate UML", e);
		}
	}
}
