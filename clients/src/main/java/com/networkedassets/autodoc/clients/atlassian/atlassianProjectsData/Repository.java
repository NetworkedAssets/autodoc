
package com.networkedassets.autodoc.clients.atlassian.atlassianProjectsData;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.MoreObjects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "slug",
        "id",
        "name",
        "scmId",
        "state",
        "statusMessage",
        "forkable",
        "project",
        "public",
        "link",
        "cloneUrl",
        "links"
})
public class Repository {

    @JsonProperty("slug")
    private String slug;
    @JsonProperty("id")
    private int id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("scmId")
    private String scmId;
    @JsonProperty("state")
    private String state;
    @JsonProperty("statusMessage")
    private String statusMessage;
    @JsonProperty("forkable")
    private boolean forkable;
    @JsonProperty("project")
    private Project project;
    @JsonProperty("public")
    private boolean _public;
    @JsonProperty("link")
    private Link link;
    @JsonProperty("cloneUrl")
    private String cloneUrl;
    @JsonProperty("links")
    private RepositoryLinks links;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The slug
     */
    @JsonProperty("slug")
    public String getSlug() {
        return slug;
    }

    /**
     * @param slug The slug
     */
    @JsonProperty("slug")
    public void setSlug(String slug) {
        this.slug = slug;
    }

    /**
     * @return The id
     */
    @JsonProperty("id")
    public int getId() {
        return id;
    }

    /**
     * @param id The id
     */
    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The scmId
     */
    @JsonProperty("scmId")
    public String getScmId() {
        return scmId;
    }

    /**
     * @param scmId The scmId
     */
    @JsonProperty("scmId")
    public void setScmId(String scmId) {
        this.scmId = scmId;
    }

    /**
     * @return The state
     */
    @JsonProperty("state")
    public String getState() {
        return state;
    }

    /**
     * @param state The state
     */
    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return The statusMessage
     */
    @JsonProperty("statusMessage")
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * @param statusMessage The statusMessage
     */
    @JsonProperty("statusMessage")
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    /**
     * @return The forkable
     */
    @JsonProperty("forkable")
    public boolean isForkable() {
        return forkable;
    }

    /**
     * @param forkable The forkable
     */
    @JsonProperty("forkable")
    public void setForkable(boolean forkable) {
        this.forkable = forkable;
    }

    /**
     * @return The project
     */
    @JsonProperty("project")
    public Project getProject() {
        return project;
    }

    /**
     * @param project The project
     */
    @JsonProperty("project")
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * @return The _public
     */
    @JsonProperty("public")
    public boolean isPublic() {
        return _public;
    }

    /**
     * @param _public The public
     */
    @JsonProperty("public")
    public void setPublic(boolean _public) {
        this._public = _public;
    }

    /**
     * @return The link
     */
    @JsonProperty("link")
    public Link getLink() {
        return link;
    }

    /**
     * @param link The link
     */
    @JsonProperty("link")
    public void setLink(Link link) {
        this.link = link;
    }

    /**
     * @return The cloneUrl
     */
    @JsonProperty("cloneUrl")
    public String getCloneUrl() {
        return cloneUrl;
    }

    /**
     * @param cloneUrl The cloneUrl
     */
    @JsonProperty("cloneUrl")
    public void setCloneUrl(String cloneUrl) {
        this.cloneUrl = cloneUrl;
    }

    /**
     * @return The links
     */
    @JsonProperty("links")
    public RepositoryLinks getLinks() {
        return links;
    }

    /**
     * @param links The links
     */
    @JsonProperty("links")
    public void setLinks(RepositoryLinks links) {
        this.links = links;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("slug", slug)
                .add("id", id)
                .add("name", name)
                .add("scmId", scmId)
                .add("state", state)
                .add("statusMessage", statusMessage)
                .add("forkable", forkable)
                .add("project", project)
                .add("_public", _public)
                .add("link", link)
                .add("cloneUrl", cloneUrl)
                .add("links", links)
                .add("additionalProperties", additionalProperties)
                .toString();
    }

}
