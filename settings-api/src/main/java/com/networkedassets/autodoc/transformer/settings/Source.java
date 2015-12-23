package com.networkedassets.autodoc.transformer.settings;

import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents code source like Stash, Bitbucket, Github and so on
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
        "id",
        "name",
        "url",
        "sourceType",
        "username",
        "password",
        "sourceExists",
        "credentialsCorrect",
        "nameCorrect",
        "sourceTypeCorrect",
        "correct",
        "projects"
})
public class Source implements Serializable {
    @JsonIgnore
    private static int totalId = 0;

    private int id;
    private String name;
    private String url;
    private SourceType sourceType;
    private String username;
    private String password;
    private boolean sourceExists;
    private boolean credentialsCorrect;
    private boolean nameCorrect;
    private boolean sourceTypeCorrect;
    public Map<String, Project> projects = new HashMap<>();

    public Source() {
        id = totalId++;
    }

    public void addProject(Project p) {
        projects.put(p.key, p);
    }

    public boolean isSourceExists() {
        return sourceExists;
    }

    public void setSourceExists(boolean sourceExists) {
        this.sourceExists = sourceExists;
    }

    public boolean isCredentialsCorrect() {
        return credentialsCorrect;
    }

    public void setCredentialsCorrect(boolean credentialsCorrect) {
        this.credentialsCorrect = credentialsCorrect;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHookKey() {
        return sourceType != null ? this.getSourceType().getHookKey() : null;
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

    public void setId(int id) {
        this.id=id;
    }
    
    
    public int getId() {
        return id;
    }

   
    public boolean isNameCorrect() {
        return nameCorrect;
    }

    
    public void setNameCorrect(boolean nameCorrect) {
        this.nameCorrect = nameCorrect;
    }

    public boolean isSourceTypeCorrect() {
        return sourceTypeCorrect;
    }

    public void setSourceTypeCorrect(boolean sourceTypeCorrect) {
        this.sourceTypeCorrect = sourceTypeCorrect;
    }

    public boolean isCorrect() {
        return isSourceExists()
                && isCredentialsCorrect()
                && isNameCorrect()
                && isSourceTypeCorrect();
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
