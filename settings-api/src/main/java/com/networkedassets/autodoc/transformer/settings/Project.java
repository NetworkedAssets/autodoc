package com.networkedassets.autodoc.transformer.settings;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a Stash or Bitbucket project
 */
public class Project implements Serializable {

	private static final long serialVersionUID = -8454844934438472607L;
	private String name;
	private String key;
	private Map<String, Repo> repos;

	public Project() {
		repos = new HashMap<>();
	}

	public Project(String name, String key) {
		this.name = name;
		this.setKey(key);
		repos = new HashMap<>();
	}

	public Project(Project otherProject) {
		this(otherProject.name, otherProject.getKey());
		repos = new HashMap<>(otherProject.repos);
	}

	public Repo getRepoBySlug(String slug) {
		return repos.get(slug);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Map<String, Repo> getRepos() {
		return repos;
	}

	public void setRepos(Map<String, Repo> repos) {
		this.repos = repos;
	}

}
