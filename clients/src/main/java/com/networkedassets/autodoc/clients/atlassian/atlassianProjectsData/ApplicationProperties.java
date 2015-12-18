
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
    "version",
    "buildNumber",
    "buildDate",
    "displayName"
})
public class ApplicationProperties {

    @JsonProperty("version")
    private String version;
    @JsonProperty("buildNumber")
    private String buildNumber;
    @JsonProperty("buildDate")
    private String buildDate;
    @JsonProperty("displayName")
    private String displayName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The version
     */
    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    /**
     * 
     * @param version
     *     The version
     */
    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 
     * @return
     *     The buildNumber
     */
    @JsonProperty("buildNumber")
    public String getBuildNumber() {
        return buildNumber;
    }

    /**
     * 
     * @param buildNumber
     *     The buildNumber
     */
    @JsonProperty("buildNumber")
    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    /**
     * 
     * @return
     *     The buildDate
     */
    @JsonProperty("buildDate")
    public String getBuildDate() {
        return buildDate;
    }

    /**
     * 
     * @param buildDate
     *     The buildDate
     */
    @JsonProperty("buildDate")
    public void setBuildDate(String buildDate) {
        this.buildDate = buildDate;
    }

    /**
     * 
     * @return
     *     The displayName
     */
    @JsonProperty("displayName")
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 
     * @param displayName
     *     The displayName
     */
    @JsonProperty("displayName")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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
