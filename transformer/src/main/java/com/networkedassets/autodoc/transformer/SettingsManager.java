package com.networkedassets.autodoc.transformer;

import com.google.common.base.Preconditions;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.transformer.clients.atlassian.HttpClientConfig;
import com.networkedassets.autodoc.transformer.clients.atlassian.api.StashClient;
import com.networkedassets.autodoc.transformer.clients.atlassian.data.Branch;
import com.networkedassets.autodoc.transformer.clients.atlassian.data.Project;
import com.networkedassets.autodoc.transformer.clients.atlassian.data.Repository;
import com.networkedassets.autodoc.transformer.settings.Settings;
import com.networkedassets.autodoc.transformer.settings.SettingsForSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Handles the settings of the application
 */
public class SettingsManager {
    private static Logger log = LoggerFactory.getLogger(SettingsManager.class);
    private Settings settings = new Settings();

    public SettingsManager() {
        //load settings from file if exist
        updateSettings();
    }

    public SettingsForSpace getSettingsForSpace(String spaceKey, String confluenceUrl) {
        updateSettings();
        SettingsForSpace settingsForSpace;
        try {
            settingsForSpace = settings.getSettingsForSpaces().stream().filter(s ->
                    (s.getSpaceKey().equals(spaceKey) && s.getConfluenceUrl().equals(confluenceUrl))).collect(Collectors.toList()).get(0);
        } catch (IndexOutOfBoundsException e) {
            settingsForSpace = getDefaultSettingsForSpace(spaceKey, confluenceUrl);
        }
        return settingsForSpace;
    }

    public void setSettingsForSpace(SettingsForSpace settingsForSpace, String spaceKey, String confluenceUrl) {
        updateSettings();
        settings.getSettingsForSpaces()
                .removeIf(s -> (s.getSpaceKey().equals(spaceKey) && s.getConfluenceUrl().equals(confluenceUrl)));
        settings.getSettingsForSpaces().add(settingsForSpace);
    }

    private void updateSettings() {
        settings.getSettingsForSpaces().stream().forEach(SettingsManager::updateProjectsFromStash);
    }

    private static SettingsForSpace getDefaultSettingsForSpace() {
        SettingsForSpace defaultSettingsForSpace = new SettingsForSpace();
        updateProjectsFromStash(defaultSettingsForSpace);
        return defaultSettingsForSpace;
    }

    private static SettingsForSpace getDefaultSettingsForSpace(String spaceKey, String confluenceUrl) {
        SettingsForSpace defaultSettingsForSpace = getDefaultSettingsForSpace();
        defaultSettingsForSpace.setConfluenceUrl(confluenceUrl);
        defaultSettingsForSpace.setSpaceKey(spaceKey);
        return defaultSettingsForSpace;
    }

    private static void updateProjectsFromStash(@Nonnull SettingsForSpace settingsForSpace) {
        Preconditions.checkNotNull(settingsForSpace);

        //TODO get stash config from the settings
        URL stashUrl;
        try {
            stashUrl = new URL("http://46.101.240.138:7990");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }
        HttpClientConfig httpClientConfig = new HttpClientConfig(stashUrl, "kcala", "admin");
        StashClient stashClient = new StashClient(httpClientConfig);
        log.debug("Stash client created");



        List<Repository> stashRepositories;
        Map<Repository, List<Branch>> branchesMap = new HashMap<>();
        try {
            stashRepositories = stashClient.getRepositories(null, null, 0, 9999).getBody().getValues();
            log.debug("REST repositories retrieved");
            for (Repository repository : stashRepositories) {
                log.debug("REST branches for {} about to be retrieved", repository.getName());
                List<Branch> branches
                        = stashClient.getRepositoryBranches(repository.getProject()
                        .getKey(), repository.getSlug(), null, 0, 9999).getBody().getValues();
                log.debug("REST branches for {} retrieved", repository.getId());
                branchesMap.put(repository, branches);
            }
        } catch (UnirestException e) {
            log.error("REST request ERROR:", e);
            return;
        }
        log.debug("REST request by StashClient handled successfully");

        Set<Project> stashProjects =
                stashRepositories.stream().map(Repository::getProject)
                        .filter(distinctByProjectKey(p -> p.getKey())).collect(Collectors.toSet());

        stashProjects.stream().forEach(project -> log.debug("Stash Project name: {}, Key: {}", project.getName(), project.getKey()));
        settingsForSpace.getProjects().stream().forEach(project -> log.debug("Settings Project name: {}, Key: {}", project.name, project.key));

        //Delete projects, repos and branches in settings, that don't exist in stash (anymore)
        settingsForSpace.getProjects().removeIf(settingsProject ->
                !stashProjects.stream().map(Project::getKey).anyMatch(key -> key.equals(settingsProject.key)));


        settingsForSpace.getProjects().forEach(project -> {
            project.repos.removeIf(repo ->
                    !stashRepositories.stream().map(stashRepo -> stashRepo.getSlug()).anyMatch(slug -> slug.equals(repo.slug)));
        });

//        settingsForSpace.getProjects().forEach(project -> {
//            project.repos.stream().forEach(repo -> {
//                repo.branches.removeIf(branch ->
//                        !branchesMap.get(repo).stream().map(Branch::getId).anyMatch(id -> id.equals(branch.id)));
//            });
//        });
    }

    public static <T> Predicate<T> distinctByProjectKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }


}
