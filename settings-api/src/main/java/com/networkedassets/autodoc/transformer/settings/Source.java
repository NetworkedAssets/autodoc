package com.networkedassets.autodoc.transformer.settings;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents code source like Stash, Bitbucket, Github and so on
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Source implements Serializable {
    // TODO: 26.11.2015 Remove default values and require user to enter them on
    // first run in frontend
    private String name;

    private String url = "http://46.101.240.138:7990";
    private SourceType sourceType = SourceType.STASH;

    private String username = "kcala";
    private String password = "admin";
    @JsonIgnore
    private Boolean sourceExists;
    @JsonIgnore
    private Boolean credentialsCorrect;
    @JsonIgnore
    private Boolean verified;
    @JsonIgnore
    private Boolean slugUnique;

    public Map<String, Project> projects = new HashMap<>();


    public void addProject(Project p) {
        projects.put(p.key, p);
    }

    public Project getProjectByKey(String key) {
        return projects.get(key);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns url-safe version of source name. Leaves only alphanumeric
     * characters and dashes ("-") Rest of the characters is changed to dash
     *
     * @return Alpanumeric characters only string
     */
    @JsonGetter("slug")
    public String getSlug() {
        return name != null ? name.replaceAll("[^A-Za-z0-9\\-]", "-") : null;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHookKey() {
        return this.getSourceType().getHookKey();
    }

    public void setHookKey(String hookKey) {
        this.getSourceType().setHookKey(hookKey);
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

    /**
     * For jackson serialization. We don't want to share password on every
     * request so we only return null on REST request
     *
     * @return null
     */
    @JsonGetter("password")
    public String getNullPassword() {
        return null;
    }

    @JsonSetter("password")
    public void setPassword(String password) {
        this.password = password;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public Boolean getSourceExists() {
        return sourceExists;
    }

    public void setSourceExists(Boolean sourceExists) {
        this.sourceExists = sourceExists;
    }

    public Boolean getCredentialsCorrect() {
        return credentialsCorrect;
    }

    public void setCredentialsCorrect(Boolean credentialsCorrect) {
        this.credentialsCorrect = credentialsCorrect;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Boolean getSlugUnique() {
        return slugUnique;
    }

    public void setSlugUnique(Boolean slugUnique) {
        this.slugUnique = slugUnique;
    }

    public static enum SourceType implements Serializable {
        STASH("com.networkedassets.atlasian.plugins.stash-postReceive-hook-plugin:postReceiveHookListener"), BITBUCKET(
                "com.networkedassets.atlassian.plugins.bitbucket-postReceive-hook-plugin:postReceiveHookListener");

        private String hookKey;

        SourceType(String hookKey) {
            this.hookKey = hookKey;
        }

        public String getHookKey() {
            return hookKey;
        }

        public void setHookKey(String hookKey) {
            this.hookKey = hookKey;
        }
    }
}
