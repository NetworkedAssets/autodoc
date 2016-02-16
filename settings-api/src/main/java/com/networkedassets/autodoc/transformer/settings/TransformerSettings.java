package com.networkedassets.autodoc.transformer.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.io.Serializable;

/**
 * Contains settings for transformer configurable from json API (e.g. from
 * Confluence)
 */
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

	private String cutAllSlashes(String url) {
		return (url.endsWith("/") || url.endsWith("\\")) ? cutAllSlashes(url.substring(0, url.length() - 1)) : url;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("address", address).toString();
	}
}
