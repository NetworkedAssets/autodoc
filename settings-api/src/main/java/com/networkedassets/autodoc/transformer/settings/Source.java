package com.networkedassets.autodoc.transformer.settings;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents code source like Stash, Bitbucket, Github and so on
 */
public class Source {
    // TODO: 26.11.2015 Remove default values and require user to enter them on first run in frontend
    private String url = "http://46.101.240.138:7990";
    private String hookKey = "com.networkedassets.atlasian.plugins.stash-postReceive-hook-plugin:postReceiveHookListener";
    private String username = "kcala";
    private String password = "admin";
    private SourceType sourceType = SourceType.STASH;
    public Map<String, Project> projects = new HashMap<>();

    public void addProject(Project p) {
        projects.put(p.key, p);
    }

    public Project getProjectByKey(String key) {
        return projects.get(key);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHookKey() {
        return hookKey;
    }

    public void setHookKey(String hookKey) {
        this.hookKey = hookKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }
}
