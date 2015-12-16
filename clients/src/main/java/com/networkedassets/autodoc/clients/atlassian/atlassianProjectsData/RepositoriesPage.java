
package com.networkedassets.autodoc.clients.atlassian.atlassianProjectsData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        "size",
        "limit",
        "isLastPage",
        "values",
        "start"
})
public class RepositoriesPage {

    @JsonProperty("size")
    private int size;
    @JsonProperty("limit")
    private int limit;
    @JsonProperty("isLastPage")
    private boolean isLastPage;
    @JsonProperty("values")
    private List<Repository> repositories = new ArrayList<Repository>();
    @JsonProperty("start")
    private int start;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The size
     */
    @JsonProperty("size")
    public int getSize() {
        return size;
    }

    /**
     * @param size The size
     */
    @JsonProperty("size")
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @return The limit
     */
    @JsonProperty("limit")
    public int getLimit() {
        return limit;
    }

    /**
     * @param limit The limit
     */
    @JsonProperty("limit")
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * @return The isLastPage
     */
    @JsonProperty("isLastPage")
    public boolean isIsLastPage() {
        return isLastPage;
    }

    /**
     * @param isLastPage The isLastPage
     */
    @JsonProperty("isLastPage")
    public void setIsLastPage(boolean isLastPage) {
        this.isLastPage = isLastPage;
    }

    /**
     * @return The repositories
     */
    @JsonProperty("repositories")
    public List<Repository> getRepositories() {
        return repositories;
    }

    /**
     * @param repositories The repositories
     */
    @JsonProperty("repositories")
    public void setRepositories(List<Repository> repositories) {
        this.repositories = repositories;
    }

    /**
     * @return The start
     */
    @JsonProperty("start")
    public int getStart() {
        return start;
    }

    /**
     * @param start The start
     */
    @JsonProperty("start")
    public void setStart(int start) {
        this.start = start;
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
                .add("size", size)
                .add("limit", limit)
                .add("isLastPage", isLastPage)
                .add("repositories", repositories)
                .add("start", start)
                .add("additionalProperties", additionalProperties)
                .toString();
    }
}
