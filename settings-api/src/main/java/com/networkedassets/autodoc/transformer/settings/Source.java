package com.networkedassets.autodoc.transformer.settings;

import com.fasterxml.jackson.annotation.*;
import com.networkedassets.autodoc.transformer.settings.view.Views;

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

    private static final long serialVersionUID = -6404862914170481264L;

    @JsonView(Views.GetSourcesView.class)
    private int id;
    @JsonView(Views.GetSourcesView.class)
    private String name;
    @JsonView(Views.GetSourcesView.class)
    private String url;
    @JsonView(Views.GetSourcesView.class)
    private SourceType sourceType;
    @JsonView(Views.GetSourcesView.class)
    private String username;
    @JsonView(Views.AddSourcePasswordView.class)
    private String password;
    @JsonView(Views.GetSourcesView.class)
    private String appLinksId;
    @JsonView(Views.SourceCorrectView.class)
    private boolean sourceExists;
    @JsonView(Views.SourceCorrectView.class)
    private boolean credentialsCorrect;
    @JsonView(Views.SourceCorrectView.class)
    private boolean nameCorrect;
    @JsonView(Views.SourceCorrectView.class)
    private boolean sourceTypeCorrect;
    @JsonView(Views.GetExpandedSourcesView.class)
    private Map<String, Project> projects = new HashMap<>();


    public Source() {
    }

    public void addProject(Project p) {
        projects.put(p.getKey(), p);
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

    public String getAppLinksId() {
        return appLinksId;
    }

    public void setAppLinksId(String appLinksId) {
        this.appLinksId = appLinksId;
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
        this.id = id;
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

    @JsonView(Views.GetSourcesView.class)
    public boolean isCorrect() {
        return isSourceExists()
                && isCredentialsCorrect()
                && isNameCorrect()
                && isSourceTypeCorrect();
    }

    public Map<String, Project> getProjects() {
        return projects;
    }

    public void setProjects(Map<String, Project> projects) {
        this.projects = projects;
    }

    public static enum SourceType implements Serializable {
        STASH("com.networkedassets.atlasian.plugins.stash-postReceive-hook-plugin:postReceiveHookListener"),
        BITBUCKET("com.networkedassets.atlassian.plugins.bitbucket-postReceive-hook-plugin:postReceiveHookListener");

        @JsonView(Views.InternalView.class)
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
