package com.networkedassets.autodoc.transformer.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.io.File;
import java.io.Serializable;

/**
 * Contains settings for transformer configurable from json API (e.g. from
 * Confluence)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransformerSettings implements Serializable {
	private static final long serialVersionUID = 2478995810617471316L;
	private String address;

	@JsonProperty("address")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address, int port) {
		this.address = cutAllSlashes(address) + ":" + port + "/event";
	}

	public void setAddress(String address, int port, String path) {
		String pathNoSlashesAtTheEnd = cutAllSlashes(path);
		if(!pathNoSlashesAtTheEnd.startsWith("/") || !pathNoSlashesAtTheEnd.startsWith("\\")){
			pathNoSlashesAtTheEnd = "/" + pathNoSlashesAtTheEnd;
		}
		this.address = cutAllSlashes(address) + ":" + port + pathNoSlashesAtTheEnd + "/event";
	}

	private String cutAllSlashes(String url) {
		return (url.endsWith("/") || url.endsWith("\\")) ? cutAllSlashes(url.substring(0, url.length() - 1)) : url;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("address", address).toString();
	}
}
