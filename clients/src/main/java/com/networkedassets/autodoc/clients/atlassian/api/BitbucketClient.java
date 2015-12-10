package com.networkedassets.autodoc.clients.atlassian.api;

import com.google.common.base.Preconditions;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.clients.atlassian.HttpClient;
import com.networkedassets.autodoc.clients.atlassian.HttpClientConfig;
import com.networkedassets.autodoc.clients.atlassian.atlassianHookData.HookConfirm;
import com.networkedassets.autodoc.clients.atlassian.atlassianHookData.HookSettings;
import com.networkedassets.autodoc.clients.atlassian.atlassianProjectsData.BranchesPage;
import com.networkedassets.autodoc.clients.atlassian.atlassianProjectsData.ProjectsPage;
import com.networkedassets.autodoc.clients.atlassian.atlassianProjectsData.RepositoriesPage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


@SuppressWarnings("Duplicates")
public class BitbucketClient extends HttpClient {

	public BitbucketClient(HttpClientConfig config) {
		super(config);
	}

	public HttpResponse<HookSettings> getHookSettings(@Nonnull final String projectKey,
													  @Nonnull final String repositorySlug, final String hookKey) throws UnirestException {

		Preconditions.checkNotNull(projectKey);
		Preconditions.checkNotNull(repositorySlug);
		Preconditions.checkNotNull(hookKey);

		String requestUrl = String.format("/rest/api/1.0/projects/%s/repos/%s/settings/hooks/%s/settings", projectKey,
				repositorySlug, hookKey);
		HttpResponse<HookSettings> jsonResponse = Unirest.get(this.getBaseUrl().toString() + requestUrl)
				.header("accept", "application/json").header("content-type", "application/json")
				.basicAuth(this.getUsername(), this.getPassword()).asObject(HookSettings.class);

		return jsonResponse;
	}

	public HttpResponse<HookSettings> setHookSettings(@Nonnull final String projectKey,
			@Nonnull final String repositorySlug, @Nonnull final String hookKey, @Nonnull final String endpointURL,
			@Nonnull final String endpointTimeout) throws UnirestException {

		Preconditions.checkNotNull(projectKey);
		Preconditions.checkNotNull(repositorySlug);
		Preconditions.checkNotNull(hookKey);
		Preconditions.checkNotNull(endpointURL);
		Preconditions.checkNotNull(endpointTimeout);

		HookSettings hookSettings = new HookSettings();
		hookSettings.setTimeout(endpointTimeout);
		hookSettings.setUrl(endpointURL);

		String requestUrl = String.format("/rest/api/1.0/projects/%s/repos/%s/settings/hooks/%s/settings", projectKey,
				repositorySlug, hookKey);
		HttpResponse<HookSettings> jsonResponse = Unirest.put(this.getBaseUrl().toString() + requestUrl)
				.basicAuth(this.getUsername(), this.getPassword()).header("accept", "application/json")
				.header("content-type", "application/json").body(hookSettings).asObject(HookSettings.class);

		return jsonResponse;
	}

	public HttpResponse<HookConfirm> setHookSettingsEnabled(@Nonnull final String projectKey,
															@Nonnull final String repositorySlug, @Nonnull final String hookKey) throws UnirestException {

		Preconditions.checkNotNull(projectKey);
		Preconditions.checkNotNull(repositorySlug);
		Preconditions.checkNotNull(hookKey);

		String requestUrl = String.format("/rest/api/1.0/projects/%s/repos/%s/settings/hooks/%s/enabled", projectKey,
				repositorySlug, hookKey);
		HttpResponse<HookConfirm> jsonResponse = Unirest.put(this.getBaseUrl().toString() + requestUrl)
				.basicAuth(this.getUsername(), this.getPassword()).header("accept", "application/json")
				.header("content-type", "application/json").asObject(HookConfirm.class);

		return jsonResponse;
	}

	public HttpResponse<HookConfirm> setHookSettingsDisabled(@Nonnull final String projectKey,
			@Nonnull final String repositorySlug, @Nonnull final String hookKey) throws UnirestException {

		Preconditions.checkNotNull(projectKey);
		Preconditions.checkNotNull(repositorySlug);
		Preconditions.checkNotNull(hookKey);

		String requestUrl = String.format("/rest/api/1.0/projects/%s/repos/%s/settings/hooks/%s/enabled", projectKey,
				repositorySlug, hookKey);
		HttpResponse<HookConfirm> jsonResponse = Unirest.delete(this.getBaseUrl().toString() + requestUrl)
				.basicAuth(this.getUsername(), this.getPassword()).header("accept", "application/json")
				.header("content-type", "application/json").asObject(HookConfirm.class);

		return jsonResponse;
	}

	public HttpResponse<ProjectsPage> getProjectsPage(@Nonnull final long start, @Nonnull final long limit) throws UnirestException {
		Preconditions.checkNotNull(start);
		Preconditions.checkNotNull(limit);

		String requestUrl = "/rest/api/1.0/projects";
		if (start > 0) {
			requestUrl += "?start=" + start;
		}
		if (limit > 0) {
			requestUrl += "&limit=" + limit;
		}

		return Unirest.get(getBaseUrl().toString() + requestUrl)
				.basicAuth(getUsername(), getPassword())
				.header("accept", "application/json")
				.asObject(ProjectsPage.class);
	}

	public HttpResponse<RepositoriesPage> getRepositoriesForProjectPage(@Nonnull final long start,
																		@Nonnull final long limit,
																		@Nonnull final String projectId) throws UnirestException {
		Preconditions.checkNotNull(start);
		Preconditions.checkNotNull(limit);
		Preconditions.checkNotNull(projectId);

		String requestUrl = String.format("/rest/api/1.0/projects/%s/repos", projectId);
		if (start > 0) {
			requestUrl += "?start=" + start;
		}
		if (limit > 0) {
			requestUrl += "&limit=" + limit;
		}

		return Unirest.get(getBaseUrl().toString() + requestUrl)
				.basicAuth(getUsername(), getPassword())
				.header("accept", "application/json")
				.asObject(RepositoriesPage.class);
	}

	public HttpResponse<BranchesPage> getRepositoryBranchesPage(final long start,
																final long limit,
																@Nonnull final String projectKey,
																@Nonnull final String repositorySlug)
			throws UnirestException {

		Preconditions.checkNotNull(start);
		Preconditions.checkNotNull(limit);
		Preconditions.checkNotNull(projectKey);
		Preconditions.checkNotNull(repositorySlug);

		String requestUrl = String.format("/rest/api/1.0/projects/%s/repos/%s/branches", projectKey, repositorySlug);
		if (start > 0) {
			requestUrl += "?start=" + start;
		}
		if (limit > 0) {
			requestUrl += "&limit=" + limit;
		}

		return Unirest.get(this.getBaseUrl().toString() + requestUrl)
				.basicAuth(this.getUsername(), this.getPassword())
				.header("accept", "application/json")
				.asObject(BranchesPage.class);

	}

}
