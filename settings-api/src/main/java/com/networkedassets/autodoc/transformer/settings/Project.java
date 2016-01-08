package com.networkedassets.autodoc.transformer.settings;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a stash project
 */
public class Project implements Serializable {

	private static final long serialVersionUID = -8454844934438472607L;
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

}
