package com.networkedassets.autodoc.transformer.handleRepoPush.core;

import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.handleRepoPush.Code;


@FunctionalInterface
public interface DocumentationGenerator {
    Documentation generateFrom(Code code);
}
