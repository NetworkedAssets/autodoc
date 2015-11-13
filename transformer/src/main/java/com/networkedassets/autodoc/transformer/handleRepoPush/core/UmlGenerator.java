package com.networkedassets.autodoc.transformer.handleRepoPush.core;

import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.util.uml.PlantUML;
import com.networkedassets.autodoc.transformer.util.uml.PlantUMLException;
import com.networkedassets.autodoc.transformer.handleRepoPush.Code;

/**
 * Created by mrobakowski on 11/13/2015.
 */
public class UmlGenerator implements DocumentationGenerator {
    @Override
    public Documentation generateFrom(Code code) {
        try {
            return new Documentation(
                    PlantUML.fromDirectory(
                            code.getCodePath(),
                            "abstract_classes,classes,extensions,implementations,imports,interfaces,native_methods,static_imports",
                            null
                    )
            );
        } catch (PlantUMLException e) {
            throw new RuntimeException("Couldn't generate UML", e);
        }
    }
}
