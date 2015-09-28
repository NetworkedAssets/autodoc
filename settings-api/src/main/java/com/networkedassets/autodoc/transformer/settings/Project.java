package com.networkedassets.autodoc.transformer.settings;

import com.google.common.collect.ImmutableMap;
import com.networkedassets.util.functional.Options;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class representing a stash project
 */
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

    public void setDefaultJavadocLocation(Long pageId) {
        repos.stream().flatMap(r -> r.branches.stream()).forEach(b -> {
            Optional<Long> currentJavadocPageId = Options.ofThrowing(() -> Long.parseLong(b.javadocPageId));
            if (!currentJavadocPageId.isPresent() || currentJavadocPageId.get().equals(-1L)) {
                b.javadocPageId = pageId.toString();
            }
        });
    }

    public void setDefaultUmlLocation(Long pageId) {
        repos.stream().flatMap(r -> r.branches.stream()).forEach(b -> {
            Optional<Long> currentUmlPageId = Options.ofThrowing(() -> Long.parseLong(b.umlPageId));
            if (!currentUmlPageId.isPresent() || currentUmlPageId.get().equals(-1L)) {
                b.umlPageId = pageId.toString();
            }
        });
    }
}
