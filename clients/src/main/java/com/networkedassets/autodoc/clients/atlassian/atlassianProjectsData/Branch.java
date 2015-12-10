
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

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "id",
        "displayId",
        "latestChangeset",
        "latestCommit",
        "isDefault"
})
public class Branch {

    @JsonProperty("id")
    private String id;
    @JsonProperty("displayId")
    private String displayId;
    @JsonProperty("latestChangeset")
    private String latestChangeset;
    @JsonProperty("latestCommit")
    private String latestCommit;
    @JsonProperty("isDefault")
    private boolean isDefault;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The id
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The displayId
     */
    @JsonProperty("displayId")
    public String getDisplayId() {
        return displayId;
    }

    /**
     * @param displayId The displayId
     */
    @JsonProperty("displayId")
    public void setDisplayId(String displayId) {
        this.displayId = displayId;
    }

    /**
     * @return The latestChangeset
     */
    @JsonProperty("latestChangeset")
    public String getLatestChangeset() {
        return latestChangeset;
    }

    /**
     * @param latestChangeset The latestChangeset
     */
    @JsonProperty("latestChangeset")
    public void setLatestChangeset(String latestChangeset) {
        this.latestChangeset = latestChangeset;
    }

    /**
     * @return The latestCommit
     */
    @JsonProperty("latestCommit")
    public String getLatestCommit() {
        return latestCommit;
    }

    /**
     * @param latestCommit The latestCommit
     */
    @JsonProperty("latestCommit")
    public void setLatestCommit(String latestCommit) {
        this.latestCommit = latestCommit;
    }

    /**
     * @return The isDefault
     */
    @JsonProperty("isDefault")
    public boolean isIsDefault() {
        return isDefault;
    }

    /**
     * @param isDefault The isDefault
     */
    @JsonProperty("isDefault")
    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
