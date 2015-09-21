package com.networkedassets.autodoc.transformer.settings;

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by mrobakowski on 9/17/2015.
 */
public class Repo {
    public String name = "!!NO_NAME!!";
    public String slug = "!!NO_NAME!!";
    public List<Branch> branches;

    public Repo() {
        branches = new ArrayList<>();
    }

    public Repo(String name, String slug) {
        this();
        this.name = name;
        this.slug = slug;
    }

    public Map<String, ?> toSoyData() {
        return ImmutableMap.of(
                "name", this.name,
                "slug", this.slug,
                "branches", this.branches.stream().map(Branch::toSoyData).collect(Collectors.toList())
        );
    }
}
