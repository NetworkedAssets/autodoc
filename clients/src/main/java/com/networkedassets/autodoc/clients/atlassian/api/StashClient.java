package com.networkedassets.autodoc.clients.atlassian.api;

import com.google.common.base.Preconditions;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.clients.atlassian.HttpClient;
import com.networkedassets.autodoc.clients.atlassian.HttpClientConfig;
import com.networkedassets.autodoc.clients.atlassian.atlassianHookData.HookConfirm;
import com.networkedassets.autodoc.clients.atlassian.atlassianHookData.HookSettings;
import com.networkedassets.autodoc.clients.atlassian.atlassianProjectsData.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("ALL")
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

    public List<Project> getProjects() throws UnirestException {
        List<Project> projects = new ArrayList<>();
        final int limit = 1000;
        int start = 0;
        HttpResponse<ProjectsPage> projectsPage;
        do {
            projectsPage = getProjectsPage(start, limit);
            start+=limit+1;
            projects.addAll(projectsPage.getBody().getProjects());
        }while (!projectsPage.getBody().isIsLastPage());
        return projects;
    }

    public List<Repository> getRepositoriesForProject(@Nonnull final String projectKey) throws UnirestException {
        List<Repository> repositories = new ArrayList<>();
        final int limit = 1000;
        int start = 0;
        HttpResponse<RepositoriesPage> repositoriesPage;
        do {
            repositoriesPage = getRepositoriesPageForProject(start, limit, projectKey);
            start+=limit+1;
            repositories.addAll(repositoriesPage.getBody().getRepositories());
        }while (!repositoriesPage.getBody().isIsLastPage());
        return repositories;
    }

    public List<Branch> getBranchesforRepository(@Nonnull final String projectKey,
                                                 @Nonnull final String repositorySlug) throws UnirestException {
        List<Branch> branches = new ArrayList<>();
        final int limit = 1000;
        int start = 0;
        HttpResponse<BranchesPage> branchesPage;
        do {
            branchesPage = getBranchesPageforRepository(start, limit,projectKey, repositorySlug);
            start+=limit+1;
            branches.addAll(branchesPage.getBody().getBranches());
        }while (!branchesPage.getBody().isIsLastPage());
        return branches;
    }

    public HttpResponse<ProjectsPage> getProjectsPage(@Nonnull final long start, @Nonnull final long limit) throws UnirestException {
        Preconditions.checkNotNull(start);
        Preconditions.checkNotNull(limit);

        String requestUrl = "/rest/api/1.0/projects";

        return Unirest.get(getBaseUrl().toString() + requestUrl)
                .queryString("start", start)
                .queryString("limit", limit)
                .basicAuth(getUsername(), getPassword())
                .header("accept", "application/json")
                .asObject(ProjectsPage.class);
    }

    public HttpResponse<RepositoriesPage> getRepositoriesPageForProject(@Nonnull final long start,
                                                                        @Nonnull final long limit,
                                                                        @Nonnull final String projectKey) throws UnirestException {
        Preconditions.checkNotNull(start);
        Preconditions.checkNotNull(limit);
        Preconditions.checkNotNull(projectKey);

        String requestUrl = String.format("/rest/api/1.0/projects/%s/repos", projectKey);

        return Unirest.get(getBaseUrl().toString() + requestUrl)
                .queryString("start", start)
                .queryString("limit", limit)
                .basicAuth(getUsername(), getPassword())
                .header("accept", "application/json")
                .asObject(RepositoriesPage.class);
    }

    public HttpResponse<BranchesPage> getBranchesPageforRepository(final long start,
                                                                   final long limit,
                                                                   @Nonnull final String projectKey,
                                                                   @Nonnull final String repositorySlug)
            throws UnirestException {

        Preconditions.checkNotNull(start);
        Preconditions.checkNotNull(limit);
        Preconditions.checkNotNull(projectKey);
        Preconditions.checkNotNull(repositorySlug);

        String requestUrl = String.format("/rest/api/1.0/projects/%s/repos/%s/branches", projectKey, repositorySlug);

        return Unirest.get(this.getBaseUrl().toString() + requestUrl)
                .queryString("start", start)
                .queryString("limit", limit)
                .basicAuth(this.getUsername(), this.getPassword())
                .header("accept", "application/json")
                .asObject(BranchesPage.class);

    }


}
