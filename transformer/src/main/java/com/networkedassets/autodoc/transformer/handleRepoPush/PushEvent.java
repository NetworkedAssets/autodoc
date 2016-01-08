package com.networkedassets.autodoc.transformer.handleRepoPush;

import com.google.common.base.MoreObjects;

/**
 * Represents event coming from Stash
 */

public class PushEvent {
	private String sourceUrl;
	private String projectKey;
	private String repositorySlug;
	private String branchId;

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("repositorySlug", repositorySlug).add("projectKey", projectKey)
				.add("branchId", branchId).toString();
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public String getRepositorySlug() {
		return repositorySlug;
	}

	public void setRepositorySlug(String repositorySlug) {
		this.repositorySlug = repositorySlug;
	}

	public String getProjectKey() {
		return projectKey;
	}

	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public boolean isValidProjectChangeEvent() {
		return sourceUrl != null && projectKey != null && repositorySlug != null && branchId != null;
	}
}
