package com.networkedassets.autodoc.transformer.handleRepoPush.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONException;

import com.networkedassets.autodoc.transformer.handleRepoPush.Code;
import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.util.javadoc.Javadoc;
import com.networkedassets.autodoc.transformer.util.javadoc.JavadocException;
import com.networkedassets.autodoc.transformer.util.uml.PlantUML;
import com.networkedassets.autodoc.transformer.util.uml.PlantUMLException;
import com.networkedassets.autodoc.transformer.util.uml.UmlClassDiagramConverter;

/**
 * Created by mrobakowski on 11/13/2015.
 */
public class UmlGenerator extends JavadocGenerator implements DocumentationGenerator {

	@Override
	public Documentation generateFrom(Code code) {

		try {

			Path plantUmlPath = PlantUML.fromDirectory(code.getCodePath(), null, null);
			Path xmlJavaDocPath = Javadoc.fromDirectory(code.getCodePath(),
					new File(getDocletFilenameFromProperties()).getAbsolutePath(), getDocletClassnameFromProperties());

			return new UmlClassDiagramConverter(
					Paths.get(xmlJavaDocPath.toString(), getDocletOutFilenameFromProperties()), plantUmlPath).convert();

		} catch (PlantUMLException | JavadocException | JSONException | IOException e)

		{
			throw new RuntimeException("Couldn't generate UML", e);
		}
	}

}
