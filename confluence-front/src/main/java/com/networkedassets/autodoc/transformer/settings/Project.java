package com.networkedassets.autodoc.transformer.settings;

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Project {
    public String name = "!!NO_NAME!!";
    public String key = "!!NO_NAME!!";
    public List<Repo> repos;

    public Project() {
        repos = new ArrayList<>();
    }

    public Project(String name, String key) {
        this.name = name;
        this.key = key;
        repos = new ArrayList<>();
    }

    public Map<String, ?> toSoyData() {
        return ImmutableMap.of(
                "name", this.name,
                "key", this.key,
                "repos", this.repos.stream().map(Repo::toSoyData).collect(Collectors.toList())
        );
    }
}
