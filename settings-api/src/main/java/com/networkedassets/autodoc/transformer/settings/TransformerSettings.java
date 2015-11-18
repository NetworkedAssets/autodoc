package com.networkedassets.autodoc.transformer.settings;

/**
 * Contains settings for transformer configurable from json API (e.g. from Confluence)
 */
public class TransformerSettings {

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
}
