package com.networkedassets.autodoc.transformer.settings;

import com.google.common.collect.ImmutableMap;
import com.networkedassets.util.functional.Optionals;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class representing a stash project
 */
public class Project implements Serializable {
    public String name = "!!NO_NAME!!";
    public String key = "!!NO_NAME!!";
    public Map<String, Repo> repos;

    public Project() {
        repos = new HashMap<>();
    }

    public Project(String name, String key) {
        this.name = name;
        this.key = key;
        repos = new HashMap<>();
    }

    public Project(Project otherProject) {
        this(otherProject.name, otherProject.key);
        repos = new HashMap<>(otherProject.repos);
    }

    public Repo getRepoBySlug(String slug) {
        return repos.get(slug);
    }

    public Map<String, ?> toSoyData() {
        return ImmutableMap.of(
                "name", this.name,
                "key", this.key,
                "repos", this.repos.values().stream().map(Repo::toSoyData).collect(Collectors.toList())
        );
    }

    public void setDefaultJavadocLocation(Long pageId) {
        repos.values().stream().flatMap(r -> r.branches.values().stream()).forEach(b -> {
            Optional<Long> currentJavadocPageId = Optionals.ofThrowing(() -> Long.parseLong(b.javadocPageId));
            if (!currentJavadocPageId.isPresent() || currentJavadocPageId.get().equals(-1L)) {
                b.javadocPageId = pageId.toString();
            }
        });
    }

    public void setDefaultUmlLocation(Long pageId) {
        repos.values().stream().flatMap(r -> r.branches.values().stream()).forEach(b -> {
            Optional<Long> currentUmlPageId = Optionals.ofThrowing(() -> Long.parseLong(b.umlPageId));
            if (!currentUmlPageId.isPresent() || currentUmlPageId.get().equals(-1L)) {
                b.umlPageId = pageId.toString();
            }
        });
    }
}
