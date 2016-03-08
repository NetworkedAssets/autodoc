package com.networkedassets.autodoc.transformer.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a repo in a Stash  or Bitbucket project
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Repo implements Serializable {

	private static final long serialVersionUID = -8959782122446629856L;
	private String name;
	private String slug;
	private Map<String, Branch> branches;

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

	
	
	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}
	
	public Map<String, Branch> getBranches() {
		return branches;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setBranches(Map<String, Branch> branches) {
		this.branches = branches;
	}
	
	public Branch getBranchById(String id) {
		return branches.get(id);
	}
}
