package com.networkedassets.autodoc.transformer.clients.atlassian.confluenceData;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Labels {
    public List<Map<String, String>> results;

    public Labels() {
    }

    public Labels(String onlyLabel) {
        results = ImmutableList.of(ImmutableMap.of("name", onlyLabel));
    }

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
