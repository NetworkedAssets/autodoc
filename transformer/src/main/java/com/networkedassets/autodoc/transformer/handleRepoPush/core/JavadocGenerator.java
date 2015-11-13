package com.networkedassets.autodoc.transformer.handleRepoPush.core;

import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.handleRepoPush.Code;
import com.networkedassets.autodoc.transformer.util.javadoc.Javadoc;
import com.networkedassets.autodoc.transformer.util.javadoc.JavadocException;

/**
 * Created by mrobakowski on 11/13/2015.
 */
public class JavadocGenerator implements DocumentationGenerator {
    @Override
    public Documentation generateFrom(Code code) {
        try {
            // TODO: Documentation object shouldn't receive the path of the docs, but the docs themselves
            return new Documentation(Javadoc.fromDirectory(code.getCodePath()).toString());
        } catch (JavadocException e) {
            throw new RuntimeException("Couldn't generate Javadoc", e);
        }
    }
}
