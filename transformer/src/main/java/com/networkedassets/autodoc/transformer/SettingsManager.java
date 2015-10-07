package com.networkedassets.autodoc.transformer;

import com.google.common.base.Preconditions;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.transformer.clients.atlassian.HttpClientConfig;
import com.networkedassets.autodoc.transformer.clients.atlassian.api.StashClient;
import com.networkedassets.autodoc.transformer.clients.atlassian.stashData.Branch;
import com.networkedassets.autodoc.transformer.clients.atlassian.stashData.Project;
import com.networkedassets.autodoc.transformer.clients.atlassian.stashData.Repository;
import com.networkedassets.autodoc.transformer.settings.Repo;
import com.networkedassets.autodoc.transformer.settings.Settings;
import com.networkedassets.autodoc.transformer.settings.SettingsForSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.*;
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

    public static final String settingsFilename = "transformerSettings.ser";
    private static final String stashUrl = "http://46.101.240.138:7990";
    private static final String stashHookKey = "com.networkedassets.atlasian.plugins.stash-postReceive-hook-plugin:postReceiveHookListener";
    private static final int transformerPort = 8050;

    public SettingsManager() {
        loadSettingFromFile(settingsFilename);
        updateSettings();
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
            stashUrl = new URL(SettingsManager.stashUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }
        HttpClientConfig httpClientConfig = new HttpClientConfig(stashUrl, "kcala", "admin");
        StashClient stashClient = new StashClient(httpClientConfig);
        log.debug("Stash client created");


        List<Repository> stashRepositories;
        Map<String, List<Branch>> branchesMap = new HashMap<>();
        try {
            stashRepositories = stashClient.getRepositories(null, null, 0, 9999).getBody().getValues();
            log.debug("REST repositories retrieved");
            for (Repository repository : stashRepositories) {
                log.debug("REST branches for {} about to be retrieved", repository.getName());
                List<Branch> branches
                        = stashClient.getRepositoryBranches(repository.getProject()
                        .getKey(), repository.getSlug(), null, 0, 9999).getBody().getValues();
                log.debug("REST branches for {} retrieved", repository.getId());
                branchesMap.put(repository.getSlug(), branches);
            }
        } catch (UnirestException e) {
            log.error("REST request ERROR:", e);
            return;
        }
        Set<Project> stashProjects =
                stashRepositories.stream().map(Repository::getProject)
                        .filter(distinctByProjectKey(Project::getKey)).collect(Collectors.toSet());
        log.debug("REST request by StashClient handled successfully");


        //Delete projects, repos and branches in settings, that don't exist in stash (anymore)
        settingsForSpace.getProjects().removeIf(settingsProject ->
                !stashProjects.stream().map(Project::getKey).anyMatch(key -> key.equals(settingsProject.key)));

        settingsForSpace.getProjects().forEach(project -> project.repos.values().removeIf(repo ->
                !stashRepositories.stream().map(Repository::getSlug).anyMatch(slug -> slug.equals(repo.slug))));

        settingsForSpace.getProjects().forEach(project ->
                project.repos.values().stream().forEach(repo ->
                        repo.branches.values().removeIf(branch -> !branchesMap.get(repo.slug).stream()
                                .map(Branch::getId).anyMatch(stashId -> stashId.equals(branch.id)))));

        //Add new projects, repos, branches from stash
        for (Project stashProject : stashProjects) {
            com.networkedassets.autodoc.transformer.settings.Project settingsProject =
                    settingsForSpace.getProjectByKey(stashProject.getKey());
            if (settingsProject == null) {
                com.networkedassets.autodoc.transformer.settings.Project newProject =
                        new com.networkedassets.autodoc.transformer.settings.Project();
                mergeProjectSettings(stashProject, newProject);
                settingsForSpace.addProject(newProject);
            } else {
                mergeProjectSettings(stashProject, settingsProject);
            }
            //repos belonging to project
            List<Repository> projectRepositories =
                    stashRepositories.stream().filter(r -> r.getProject()
                            .getKey().equals(stashProject.getKey())).collect(Collectors.toList());

            for (Repository stashRepository : projectRepositories) {
                Repo settingsRepository = settingsForSpace.getProjectByKey(stashProject.getKey()).getRepoBySlug(stashRepository.getSlug());
                if (settingsRepository == null) {
                    Repo newRepo = new Repo();
                    mergeRepositorySettings(stashRepository, newRepo);
                    settingsForSpace.getProjectByKey(stashProject.getKey()).repos.put(newRepo.slug, newRepo);
                } else {
                    mergeRepositorySettings(stashRepository, settingsRepository);
                }
                List<Branch> repositoryBranches = branchesMap.get(stashRepository.getSlug());
                for (Branch stashBranch : repositoryBranches) {
                    com.networkedassets.autodoc.transformer.settings.Branch settingsBranch =
                            settingsForSpace.getProjectByKey(stashProject.getKey()).
                                    getRepoBySlug(stashRepository.getSlug())
                                    .getBranchById(stashBranch.getId());
                    if (settingsBranch == null) {
                        com.networkedassets.autodoc.transformer.settings.Branch newBranch =
                                new com.networkedassets.autodoc.transformer.settings.Branch();
                        mergeBranchSettings(stashBranch, newBranch);
                        settingsForSpace.getProjectByKey(stashProject.getKey())
                                .getRepoBySlug(stashRepository.getSlug()).branches.put(newBranch.id, newBranch);
                    } else {
                        mergeBranchSettings(stashBranch, settingsBranch);
                    }
                }
            }

        }

    }

    /**
     * Merges stash branch into the settings branch
     *
     * @param stashBranch    This branch's attributes will be merged into settings branch
     * @param settingsBranch This branch will be changed, by inserting some of the stash branch attributes into it
     */
    public static void mergeBranchSettings(
            Branch stashBranch, com.networkedassets.autodoc.transformer.settings.Branch settingsBranch) {
        settingsBranch.displayId = stashBranch.getDisplayId();
        settingsBranch.id = stashBranch.getId();
    }

    /**
     * Merges stash repository into the settings repository
     *
     * @param stashRepository    This repository's attributes will be merged into settings repository
     * @param settingsRepository This repository will be changed, by inserting some of the stash repository attributes into it
     */
    public static void mergeRepositorySettings(Repository stashRepository, Repo settingsRepository) {
        settingsRepository.name = stashRepository.getName();
        settingsRepository.slug = stashRepository.getSlug();
    }

    /**
     * Merges stash project into the settings project
     *
     * @param stashProject    This project's attributes will be merged into settings project
     * @param settingsProject This project will be changed, by inserting some of the stash project attributes into it
     */
    public static void mergeProjectSettings(
            Project stashProject, com.networkedassets.autodoc.transformer.settings.Project settingsProject) {
        settingsProject.name = stashProject.getName();
        settingsProject.key = stashProject.getKey();
    }

    public static <T> Predicate<T> distinctByProjectKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public SettingsForSpace getSettingsForSpace(String spaceKey, String confluenceUrl) {
        updateSettings();
        SettingsForSpace settingsForSpace;
        settingsForSpace = settings.getSettingsForSpaces().stream().filter(s ->
                (s.getSpaceKey().equals(spaceKey) && s.getConfluenceUrl().equals(confluenceUrl)))
                .findFirst().orElse(getDefaultSettingsForSpace(spaceKey, confluenceUrl));

        return settingsForSpace;
    }

    public List<SettingsForSpace> getSettingsForSpaces() {
        updateSettings();
        return settings.getSettingsForSpaces();
    }

    public void setSettingsForSpace(SettingsForSpace settingsForSpace, String spaceKey, String confluenceUrl) {
        settings.getSettingsForSpaces()
                .removeIf(s -> (s.getSpaceKey().equals(spaceKey) && s.getConfluenceUrl().equals(confluenceUrl)));
        settings.getSettingsForSpaces().add(settingsForSpace);
        updateSettings();
        saveSettingsToFile(settingsFilename);
    }

    private void updateSettings() {
        settings.getSettingsForSpaces().stream().forEach((settingsForSpace) -> {
            SettingsManager.updateProjectsFromStash(settingsForSpace);
            enableAllHooks(settingsForSpace);
        });
    }

    private void saveSettingsToFile(String filename) {
        File settingsFile = new File(filename);
        try (
                FileOutputStream fileOut = new FileOutputStream(settingsFile);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)
        ) {
            objectOut.writeObject(settings);
            log.debug("Settings saved to {}", settingsFile.getAbsolutePath());
        } catch (FileNotFoundException e) {
            log.error("Can't save settings to {} because he file was not found: ", settingsFile.getAbsolutePath(), e);
        } catch (IOException e) {
            log.error("Can't save settings to {} because of general IO problem: ", settingsFile.getAbsolutePath(), e);
        }
    }

    private void loadSettingFromFile(String filename) {
        File settingsFile = new File(filename);
        try (
                FileInputStream fileIn = new FileInputStream(settingsFile);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn)
        ) {
            settings = (Settings) objectIn.readObject();
            log.error("Settings loaded from {}", settingsFile.getAbsolutePath());
        } catch (FileNotFoundException e) {
            log.error("Can't load settings from {} because he file was not found: ", settingsFile.getAbsolutePath(), e);
        } catch (ClassNotFoundException e) {
            log.error("Can't load settings from {} because he file is corrupted or of incompatible version: ", settingsFile.getAbsolutePath(), e);
        } catch (IOException e) {
            log.error("Can't load settings from {} because general IO problem: ", settingsFile.getAbsolutePath(), e);
        }
    }

    public void enableAllHooks(SettingsForSpace settingsForSpace){
        URL stashUrl;
        try {
            stashUrl = new URL(SettingsManager.stashUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }
        HttpClientConfig httpClientConfig = new HttpClientConfig(stashUrl, "kcala", "admin");
        StashClient stashClient = new StashClient(httpClientConfig);
        log.debug("Stash client created");

        final String localhostAddress = "http://localhost:" + transformerPort + "/event";

        settingsForSpace.getProjects().stream().forEach(project -> project.repos.values().stream().forEach(repo -> {
            try {
                stashClient.setHookSettings(project.key, repo.slug, stashHookKey, localhostAddress, "30000");
                stashClient.setHookSettingsEnabled(project.key, repo.slug, stashHookKey);
            } catch (UnirestException e) {
                log.error("Error while activating hooks for {}/{}: ",project.name, repo.slug, e);
            }
        }));
    }


}
