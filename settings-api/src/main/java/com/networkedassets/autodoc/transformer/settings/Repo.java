package com.networkedassets.autodoc.transformer.settings;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a repo in a stash project
 */
public class Repo implements Serializable {
	public String name = "!!NO_NAME!!";
	public String slug = "!!NO_NAME!!";
	public Map<String, Branch> branches;

	public Repo() {
		branches = new HashMap<>();
	}

	public Repo(String name, String slug) {
		this();
		this.name = name;
		this.slug = slug;
	}

	public Repo(Repo otherRepo) {
		this(otherRepo.name, otherRepo.slug);
		this.branches = new HashMap<>(otherRepo.branches);
	}

	public Branch getBranchById(String id) {
		return branches.get(id);
	}
}
