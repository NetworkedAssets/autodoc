package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;


import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.clients.atlassian.api.StashClient;
import com.networkedassets.autodoc.clients.atlassian.stashData.Repository;
import com.networkedassets.autodoc.transformer.manageSettings.require.ProjectsProvider;
import com.networkedassets.autodoc.transformer.settings.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Pulls projects info from stash and translates it to SettingsAPI format
 */
public class StashProjectsProvider implements ProjectsProvider {

    private static Logger log = LoggerFactory.getLogger(StashProjectsProvider.class);
    Source source;
    StashClient stashClient;

    private Map<String, Project> projects;

    public StashProjectsProvider(Source source) throws MalformedURLException {
        this.source = source;
        stashClient = ClientConfigurator.getConfiguredStashClient(source);
    }

    @Override
    public Map<String, Project> getProjects() {
        getDataFromStash();
        return projects;
    }

    private void getDataFromStash() {
        List<Repository> stashRepositories = new ArrayList<>();
        Map<String, List<com.networkedassets.autodoc.clients.atlassian.stashData.Branch>>
                stashBranchesMap = new HashMap<>();
        List<com.networkedassets.autodoc.clients.atlassian.stashData.Project> stashProjects;

        //get repositories
        try {
            stashRepositories = stashClient.getRepositories(null, null, 0, 9999).getBody().getValues();
        } catch (UnirestException e) {
            log.error("Can't retrieve repositories from stash: ", e);
        }
        log.debug("Repositories retrieved from Stash");

        //get branches basing on retrieved repositories
        try {
            for (Repository repository : stashRepositories) {
                log.debug("REST branches for {} about to be retrieved", repository.getName());
                List<com.networkedassets.autodoc.clients.atlassian.stashData.Branch> branches
                        = stashClient.getRepositoryBranches(repository.getProject()
                        .getKey(), repository.getSlug(), null, 0, 9999).getBody().getValues();
                log.debug("REST branches for {} retrieved", repository.getId());
                stashBranchesMap.put(repository.getSlug(), branches);
            }
        } catch (UnirestException e) {
            log.error("Can't retrieve branches from stash:", e);
        }

        //get projects basing on retrieved branches
        stashProjects = stashRepositories.stream().map(Repository::getProject)
                .filter(distinctByField(com.networkedassets.autodoc.clients.atlassian.stashData.Project::getKey))
                .collect(Collectors.toList());
        this.projects = translateStashDataToSettingsApi(stashProjects,stashRepositories,stashBranchesMap);
    }

    private Map<String, Project> translateStashDataToSettingsApi(
            List<com.networkedassets.autodoc.clients.atlassian.stashData.Project> stashProjects,
            List<Repository> stashRepositories,
            Map<String, List<com.networkedassets.autodoc.clients.atlassian.stashData.Branch>> stashBranchesMap
    ) {

        Map<String, Project> projects = new HashMap<>();

        //translate projects
        stashProjects.forEach(stashProject -> {
//            projects.add(new Project(stashProject.getName(), stashProject.getKey()))
            projects.put(stashProject.getKey(), new Project(stashProject.getName(), stashProject.getKey()));
        });

        //translate repos
        stashRepositories.forEach(stashRepository -> {
            Repo repo = new Repo(stashRepository.getName(), stashRepository.getSlug());
            String projectKey = stashRepository.getProject().getKey();
            projects.get(projectKey).repos.put(repo.slug, repo);
        });

        //translate branches
        stashBranchesMap.forEach((s, branches) -> branches.forEach(stashBranch -> {
            Branch branch = new Branch(stashBranch.getDisplayId(), stashBranch.getId());
            String projectKey = stashRepositories.stream()
                    .filter(r -> r.getSlug().equals(s))
                    .map(Repository::getProject)
                    .map(com.networkedassets.autodoc.clients.atlassian.stashData.Project::getKey)
                    .findAny().orElse(null);
            projects.get(projectKey).repos.get(s).branches.put(branch.id, branch);
        }));

        return projects;
    }

    public static <T> Predicate<T> distinctByField(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
