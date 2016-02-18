package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;


import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.clients.atlassian.api.StashBitbucketClient;
import com.networkedassets.autodoc.clients.atlassian.atlassianProjectsData.Repository;
import com.networkedassets.autodoc.transformer.manageSettings.require.ProjectsProvider;
import com.networkedassets.autodoc.transformer.settings.Branch;
import com.networkedassets.autodoc.transformer.settings.Project;
import com.networkedassets.autodoc.transformer.settings.Repo;
import com.networkedassets.autodoc.transformer.settings.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pulls projects info from stash and translates it to SettingsAPI format
 */
public class AtlassianProjectsProvider implements ProjectsProvider {

    private static Logger log = LoggerFactory.getLogger(AtlassianProjectsProvider.class);
    Source source;
    StashBitbucketClient stashBitbucketClient;

    private Map<String, Project> projects = new HashMap<>();

    public AtlassianProjectsProvider(Source source) throws MalformedURLException {
        this.source = source;
        stashBitbucketClient = ClientFactory.getConfiguredStashBitbucketClient(source);
    }

    @Override
    public Map<String, Project> getProjects() {
        fetchDataFromStash();
        return projects;
    }


    private void fetchDataFromStash() {
        try {
            fetchProjects();
            fetchRepos();
            fetchBranches();
        } catch (UnirestException e) {
            log.error("Exception: ", e);
        }
    }

    private void fetchBranches() throws UnirestException {
        for (Project project : projects.values()) {
            for (Repo repo : project.getRepos().values()) {
                stashBitbucketClient.getBranchesforRepository(project.getKey(), repo.getSlug())
                        .forEach(sourceBranch ->
                                repo.getBranches().put(
                                        sourceBranch.getId(),
                                        new Branch(sourceBranch.getDisplayId(), sourceBranch.getId())
                                )
                        );
            }
        }
    }

    private void fetchRepos() throws UnirestException {
        for (Project project : projects.values()) {
            List<Repository> sourceRepositories = stashBitbucketClient.getRepositoriesForProject(project.getKey());
            for (Repository sourceRepository : sourceRepositories) {
                project.getRepos().put(sourceRepository.getSlug(), new Repo(sourceRepository.getName(), sourceRepository.getSlug()));
            }
        }
    }

    private void fetchProjects() throws UnirestException {
        stashBitbucketClient.getProjects().forEach(sourceProject ->
                projects.put(sourceProject.getKey(), new Project(sourceProject.getName(), sourceProject.getKey())));
        log.debug("REST projects retrieved from {}", this.source.getUrl());
    }
}
