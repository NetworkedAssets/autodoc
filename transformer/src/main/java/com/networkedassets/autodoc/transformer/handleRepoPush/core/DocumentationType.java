package com.networkedassets.autodoc.transformer.handleRepoPush.core;

public enum DocumentationType {
	JAVADOC,
	UML;

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}
