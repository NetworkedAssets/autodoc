package com.networkedassets.autodoc.transformer.clients.atlassian.api;

import com.google.common.base.Preconditions;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.transformer.clients.atlassian.HttpClient;
import com.networkedassets.autodoc.transformer.clients.atlassian.HttpClientConfig;
import com.networkedassets.autodoc.transformer.clients.atlassian.stashData.HookConfirm;
import com.networkedassets.autodoc.transformer.clients.atlassian.stashData.HookSettings;
import com.networkedassets.autodoc.transformer.clients.atlassian.stashData.Page;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StashClient extends HttpClient {

	public StashClient(HttpClientConfig config) {
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

	public HttpResponse<Page> getRepositories(@Nullable final String projectKey, @Nullable final String query,
			@Nonnull final long start, @Nonnull final long limit) throws UnirestException {

		Preconditions.checkNotNull(start);
		Preconditions.checkNotNull(limit);
		String requestUrl = String.format("/rest/api/1.0/repos?start=%d", start);
		if (limit > 0) {
			requestUrl += "&limit=" + limit;
		}
		if (!isBlank(projectKey)) {
			requestUrl += "&projectname=" + encode(projectKey);
		}
		if (!isBlank(query)) {
			requestUrl += "&name=" + encode(query);
		}

		HttpResponse<Page> jsonResponse = Unirest.get(this.getBaseUrl().toString() + requestUrl)
				.basicAuth(this.getUsername(), this.getPassword()).header("accept", "application/json")
				.asObject(Page.class);

		return jsonResponse;
	}

	public HttpResponse<Page> getRepositoryBranches(@Nonnull final String projectKey,
			@Nonnull final String repositorySlug, @Nullable final String query, final long start, final long limit)
					throws UnirestException {

		Preconditions.checkNotNull(start);
		Preconditions.checkNotNull(limit);
		String requestUrl = String.format(
				"/rest/api/1.0/projects/%s/repos/%s/branches?start=%d&details=true&orderBy=MODIFICATION", projectKey,
				repositorySlug, start);
		if (limit > 0) {
			requestUrl += "&limit=" + limit;
		}
		if (!isBlank(query)) {
			requestUrl += "&filterText=" + encode(query);
		}

		HttpResponse<Page> jsonResponse = Unirest.get(this.getBaseUrl().toString() + requestUrl)
				.basicAuth(this.getUsername(), this.getPassword()).header("accept", "application/json")
				.asObject(Page.class);

		return jsonResponse;
	}

}
