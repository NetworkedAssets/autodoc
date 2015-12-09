package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;


import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.clients.atlassian.api.BitbucketClient;
import com.networkedassets.autodoc.clients.atlassian.stashData.Repository;
import com.networkedassets.autodoc.transformer.manageSettings.require.ProjectsProvider;
import com.networkedassets.autodoc.transformer.settings.Branch;
import com.networkedassets.autodoc.transformer.settings.Project;
import com.networkedassets.autodoc.transformer.settings.Repo;
import com.networkedassets.autodoc.transformer.settings.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Pulls projects info from stash and translates it to SettingsAPI format
 */
@SuppressWarnings("Duplicates")
public class BitbucketProjectsProvider implements ProjectsProvider {

    private static Logger log = LoggerFactory.getLogger(BitbucketProjectsProvider.class);
    Source source;
    BitbucketClient bitbucketClient;

    private Map<String, Project> projects;

    public BitbucketProjectsProvider(Source source) throws MalformedURLException {
        this.source = source;
        bitbucketClient = ClientConfigurator.getConfiguredBitbucketClient(source);
    }

    @Override
    public Map<String, Project> getProjects() {
        getDataFromBitbucket();
        return projects;
    }

    private void getDataFromBitbucket() {
        List<Repository> bitbucketRepositories = new ArrayList<>();
        Map<String, List<com.networkedassets.autodoc.clients.atlassian.stashData.Branch>>
                bitbucketBranchesMap = new HashMap<>();
        List<com.networkedassets.autodoc.clients.atlassian.stashData.Project> bitbucketProjects;

        //get repositories
        try {
            bitbucketRepositories = bitbucketClient.getRepositories(null, null, 0, 9999).getBody().getValues();
        } catch (UnirestException e) {
            log.error("Can't retrieve repositories from bitbucket: ", e);
        }
        log.debug("Repositories retrieved from Bitbucket");

        //get branches basing on retrieved repositories
        try {
            for (Repository repository : bitbucketRepositories) {
                log.debug("REST branches for {} about to be retrieved", repository.getName());
                List<com.networkedassets.autodoc.clients.atlassian.stashData.Branch> branches
                        = bitbucketClient.getRepositoryBranches(repository.getProject()
                        .getKey(), repository.getSlug(), null, 0, 9999).getBody().getValues();
                log.debug("REST branches for {} retrieved", repository.getId());
                bitbucketBranchesMap.put(repository.getSlug(), branches);
            }
        } catch (UnirestException e) {
            log.error("Can't retrieve branches from Bitbucket:", e);
        }

        //get projects basing on retrieved branches
        bitbucketProjects = bitbucketRepositories.stream().map(Repository::getProject)
                .filter(distinctByField(com.networkedassets.autodoc.clients.atlassian.stashData.Project::getKey))
                .collect(Collectors.toList());
        this.projects = translateBitbucketDataToSettingsApi(bitbucketProjects, bitbucketRepositories, bitbucketBranchesMap);
    }

    private Map<String, Project> translateBitbucketDataToSettingsApi(
            List<com.networkedassets.autodoc.clients.atlassian.stashData.Project> bitbucketProjects,
            List<Repository> bitbucketRepositories,
            Map<String, List<com.networkedassets.autodoc.clients.atlassian.stashData.Branch>> bitbucketBranchesMap
    ) {

        Map<String, Project> projects = new HashMap<>();

        //translate projects
        bitbucketProjects.forEach(bitbucketProject -> {
            projects.put(bitbucketProject.getKey(), new Project(bitbucketProject.getName(), bitbucketProject.getKey()));
        });

        //translate repos
        bitbucketRepositories.forEach(bitbucketRepository -> {
            Repo repo = new Repo(bitbucketRepository.getName(), bitbucketRepository.getSlug());
            String projectKey = bitbucketRepository.getProject().getKey();
            projects.get(projectKey).repos.put(repo.slug, repo);
        });

        //translate branches
        bitbucketBranchesMap.forEach((s, branches) -> branches.forEach(bitbucketBranch -> {
            Branch branch = new Branch(bitbucketBranch.getDisplayId(), bitbucketBranch.getId());
            String projectKey = bitbucketRepositories.stream()
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
