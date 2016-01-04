package com.networkedassets.autodoc.transformer.manageSettings.provide.in;

public interface SourceRemover {
	/**
	 * Removes all sources with matching id or url
	 * 
	 * @param source
	 *            - should contain id OR url of the source meant to be removed
	 * @return true if the source was removed
	 */
	boolean removeSource(int sourceId);
}
