package com.networkedassets.autodoc.transformer.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.io.Serializable;

/**
 * Contains settings for transformer configurable from json API (e.g. from Confluence)
 */
public class TransformerSettings implements Serializable {

    // TODO: 19.11.2015 Remove default values and require user to enter them on first run in frontend
    private int transformerPort = 8050;
    private String localhostAddress = "http://localhost:" + transformerPort + "/event";


    public int getTransformerPort() {
        return transformerPort;
    }

    public void setTransformerPort(int transformerPort) {
        this.transformerPort = transformerPort;
    }

    public String getLocalhostAddress() {
        return localhostAddress;
    }

    public void setLocalhostAddress(String localhostAddress) {
        this.localhostAddress = localhostAddress;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("transformerPort", transformerPort)
                .add("localhostAddress", localhostAddress)
                .toString();
    }
}
