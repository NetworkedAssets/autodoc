package com.networkedassets.autodoc.transformer.clients.atlassian.confluenceData;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by mrobakowski on 9/28/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Labels {
    public List<Map<String, String>> results;

    @JsonIgnore
    public Stream<String> labels() {
        return results.stream().map(m -> m.get("name"));
    }

    @Override
    public String toString() {
        return "Labels{" +
                "results=" + results +
                '}';
    }
}
