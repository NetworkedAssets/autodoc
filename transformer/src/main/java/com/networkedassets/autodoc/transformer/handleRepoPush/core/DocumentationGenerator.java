package com.networkedassets.autodoc.transformer.handleRepoPush.core;

import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.handleRepoPush.Code;

/**
 * Created by mrobakowski on 11/12/2015.
 */
@FunctionalInterface
public interface DocumentationGenerator {
    Documentation generateFrom(Code code);
}
