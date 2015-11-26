package com.networkedassets.autodoc.transformer.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.io.Serializable;

/**
 * Contains settings for transformer configurable from json API (e.g. from Confluence)
 */
public class TransformerSettings implements Serializable {

    // TODO: 19.11.2015 Remove default values and require user to enter them on first run in frontend
    private  String stashUrl = "http://46.101.240.138:7990";
    private  String stashHookKey = "com.networkedassets.atlasian.plugins.stash-postReceive-hook-plugin:postReceiveHookListener";
    private  String stashUsername = "kcala";
    private  String stashPassword = "admin";
    private  int transformerPort = 8050;
    private  String localhostAddress = "http://localhost:" + transformerPort + "/event";

    public String getStashUrl() {
        return stashUrl;
    }

    public void setStashUrl(String stashUrl) {
        this.stashUrl = stashUrl;
    }

    public String getStashHookKey() {
        return stashHookKey;
    }

    public void setStashHookKey(String stashHookKey) {
        this.stashHookKey = stashHookKey;
    }

    public String getStashUsername() {
        return stashUsername;
    }

    public void setStashUsername(String stashUsername) {
        this.stashUsername = stashUsername;
    }

    public String getStashPassword() {
        return stashPassword;
    }

    /**
     * For jackson serialization. We don't want to share password on every request so we only
     * return null on REST request
     * @return null
     */
    @JsonProperty("stashPassword")
    public String getNullStashPassword() {
        return null;
    }

    public void setStashPassword(String stashPassword) {
        this.stashPassword = stashPassword;
    }

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
                .add("stashUrl", stashUrl)
                .add("stashHookKey", stashHookKey)
                .add("stashUsername", stashUsername)
                .add("stashPassword", stashPassword)
                .add("transformerPort", transformerPort)
                .add("localhostAddress", localhostAddress)
                .toString();
    }
}
