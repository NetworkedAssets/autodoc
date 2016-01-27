package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;


import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.clients.atlassian.api.StashBitbucketClient;
import com.networkedassets.autodoc.clients.atlassian.atlassianProjectsData.*;
import com.networkedassets.autodoc.transformer.manageSettings.require.ProjectsProvider;
import com.networkedassets.autodoc.transformer.settings.*;
import com.networkedassets.autodoc.transformer.settings.Branch;
import com.networkedassets.autodoc.transformer.settings.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.*;

/**
 * Pulls projects info from stash and translates it to SettingsAPI format
 */
@SuppressWarnings("Duplicates")
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
        getDataFromStash();
        return projects;
    }

    private void getDataFromStash() {

        List<com.networkedassets.autodoc.clients.atlassian.atlassianProjectsData.Project> sourceProjects
                = new ArrayList<>();

        //get all projects
        try {
            sourceProjects = stashBitbucketClient.getProjects();
            log.debug("REST projects retrieved from {}", this.source.getUrl());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        sourceProjects.stream().forEach(sourceProject -> {
            projects.put(sourceProject.getKey(), new Project(sourceProject.getName(), sourceProject.getKey()));
        });

        //get repos for projects
        projects.values().stream().forEach(project -> {
            try {
                List<Repository> sourceRepositories = stashBitbucketClient.getRepositoriesForProject(project.key);
                sourceRepositories.stream().forEach(sourceRepository -> {
                    project.repos.put(sourceRepository.getSlug(), new Repo(sourceRepository.getName(), sourceRepository.getSlug()));
                });
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        });

        //get branches for repos
        projects.values().stream().forEach(project -> {
            project.repos.values().forEach(repo -> {
                try {
                    List<com.networkedassets.autodoc.clients.atlassian.atlassianProjectsData.Branch> sourceBranches
                            = stashBitbucketClient.getBranchesforRepository(project.key, repo.slug);
                    sourceBranches.forEach(sourceBranch -> {
                        project.repos.get(repo.slug).branches.put(sourceBranch.getId(),
                                new Branch(sourceBranch.getDisplayId(), sourceBranch.getId()));
                    });
                } catch (UnirestException e) {
                    e.printStackTrace();
                }
            });
        });
    }
}
