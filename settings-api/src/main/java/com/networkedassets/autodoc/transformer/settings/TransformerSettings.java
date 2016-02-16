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
	private int transformerPort = 8050;
	private String localhostAddress = "https://localhost:" + transformerPort + "/event";

	@JsonProperty("transformerPort")
	public int getTransformerPort() {
		return transformerPort;
	}

	public void setTransformerPort(int transformerPort) {
		this.transformerPort = transformerPort;
	}

	@JsonProperty("localhostAddress")
	public String getLocalhostAddress() {
		return localhostAddress;
	}

	public void setLocalhostAddress(String localhostAddress, int transformerPort) {
		this.transformerPort = transformerPort;
		this.localhostAddress = cutAllSlashes(localhostAddress) + "asdasdsda:" + transformerPort + "/event";
	}

	private String cutAllSlashes(String url) {
		if(url.endsWith("/") || url.endsWith("\\")){
			return cutAllSlashes(url.substring(0, url.length() - 1));
		}else{
			return url;
		}
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("transformerPort", transformerPort)
				.add("localhostAddress", localhostAddress).toString();
	}
}
