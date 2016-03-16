package com.networkedassets.autodoc.transformer.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

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

	public void setAddress(String host, int port) {
		this.address = cutAllSlashes(host) + ":" + port + "/event";
	}

	public void setAddress(String host, int port, String path) {
		this.address = cutAllSlashes(host) + ":" + port + ((path != null && !path.isEmpty()) ? "/" + cutAllSlashes(path) : "") + "/event";
	}

    private String cutAllSlashes(String toCut) {
        return cutPrefixSlashes(cutSuffixSlashes(toCut));
    }

    private String cutPrefixSlashes(String toCut) {
        return (toCut.startsWith("/") || toCut.startsWith("\\")) ? cutPrefixSlashes(toCut.substring(1, toCut.length())) : toCut;
    }

    private String cutSuffixSlashes(String toCut) {
        return (toCut.endsWith("/") || toCut.endsWith("\\")) ? cutSuffixSlashes(toCut.substring(0, toCut.length() - 1)) : toCut;
    }

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("address", address).toString();
	}
}
