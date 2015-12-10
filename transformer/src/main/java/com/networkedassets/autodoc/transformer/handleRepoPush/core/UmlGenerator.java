package com.networkedassets.autodoc.transformer.handleRepoPush.core;

import com.networkedassets.autodoc.transformer.handleRepoPush.Code;
import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.util.javadoc.JavadocException;
import com.networkedassets.autodoc.transformer.util.uml.PlantUML;
import com.networkedassets.autodoc.transformer.util.uml.PlantUMLException;
import com.networkedassets.autodoc.transformer.util.uml.UmlClassDiagramConverter;
import org.json.JSONException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by mrobakowski on 11/13/2015.
 */
public class UmlGenerator extends JavadocGenerator implements DocumentationGenerator {

	@Override
	public Documentation generateFrom(Code code) {
		try {
			Path plantUmlPath = PlantUML.fromDirectory(code.getCodePath(), null, null);
			return new UmlClassDiagramConverter(plantUmlPath, code).convert();
		} catch (PlantUMLException | JSONException | IOException | JAXBException | JavadocException e) {
			throw new RuntimeException("Couldn't generate UML", e);
		}
	}
}
