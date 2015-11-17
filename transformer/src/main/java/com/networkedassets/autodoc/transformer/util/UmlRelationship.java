package com.networkedassets.autodoc.transformer.util;

import java.util.Arrays;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

public enum UmlRelationship {

	association("-->"), 
	dependency("..>"), 
	aggregation("o--"), 
	composition("*--"), 
	generalization("--|>"), 
	realisation("..|>");

	private static final Map<String, UmlRelationship> LOOKUP = Maps.uniqueIndex(Arrays.asList(UmlRelationship.values()),
			UmlRelationship::getDescription);

	private final String value;

	private UmlRelationship(String value) {
		this.value = value;
	}

	public String getDescription() {

		return this.value;
	}

	@Nullable
	public static UmlRelationship fromDescription(String value) {
		return LOOKUP.get(value);
	}

};
