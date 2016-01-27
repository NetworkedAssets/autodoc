package com.networkedassets.autodoc.transformer.util;

import com.google.common.base.Strings;
import com.networkedassets.autodoc.clients.atlassian.api.StashBitbucketClient;
import com.networkedassets.autodoc.transformer.manageSettings.infrastructure.ClientFactory;
import com.networkedassets.autodoc.transformer.manageSettings.infrastructure.ProjectsProviderFactory;
import com.networkedassets.autodoc.transformer.manageSettings.require.ProjectsProvider;
import com.networkedassets.autodoc.transformer.settings.Project;
import com.networkedassets.autodoc.transformer.settings.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Variable utils useful when dealing with transformer's settings
 */
public final class SettingsUtils {

    private static Logger log = LoggerFactory.getLogger(SettingsUtils.class);

    private SettingsUtils() {
    }

    /**
     * Gets projects from remote source, correlated with given one and integrate them into it
     *
     * @param source will have its projects changed to match remote source (added, removed or updated)
     * @throws MalformedURLException in case source's url is malformed s
     */
    public static void updateProjectsFromRemoteSource(@Nonnull Source source) throws MalformedURLException {
        // Get projects from remote source
        ProjectsProvider projectsProvider = ProjectsProviderFactory.getInstance(source);
        Map<String, Project> sourceProjects = projectsProvider.getProjects();

        //Integrate them into the local source
        constrainSourceWithGivenProjectsData(source, sourceProjects);
        addAndUpdateProjectsToSource(source, sourceProjects);
        updateSourceNonIndexingData(source, sourceProjects);
    }

    /**
     * Copies additional data from given projects to source
     * <p>
     * Additional data means projects' names and branches' displayIds
     *
     * @param source   will be updated with given projects
     * @param projects origin from which source will be updated
     */
    public static void updateSourceNonIndexingData(@Nonnull Source source, Map<String, Project> projects) {

        source.projects.values().stream()
                .filter(project -> projects.containsKey(project.key))
                .forEach(project -> project.name = projects.get(project.key).name);

        source.projects.values().stream()
                .filter(project -> projects.containsKey(project.key))
                .forEach(project -> project.repos.values().stream()
                        .filter(repo -> projects.get(project.key).repos.containsKey(repo.slug))
                        .forEach(repo -> repo.branches.values().stream()
                                .filter(branch -> projects.get(project.key)
                                        .repos.get(repo.slug).branches.containsKey(branch.getId()))
                                .forEach(branch -> branch.setDisplayId(projects.get(project.key)
                                        .repos.get(repo.slug).branches.get(branch.getId()).getDisplayId()))));
    }

    /**
     * Add given projects, repos and branches to the source
     * <p>
     * Given projects that don't yet exist in the source will be added. The rest will be updated if they
     * have repos or branches missing
     *
     * @param source   will be updated with given projects
     * @param projects origin from which source will be updated
     */
    public static void addAndUpdateProjectsToSource(@Nonnull Source source, Map<String, Project> projects) {
        // Add new branches, repos, and projects from stash
        projects.values().forEach(stashProject -> {
            source.projects.putIfAbsent(stashProject.key, stashProject);
        });
        projects.values().forEach(stashProject -> {
            stashProject.repos.values().forEach(stashRepo -> {
                source.getProjectByKey(stashProject.key).repos.putIfAbsent(stashRepo.slug, stashRepo);
            });
        });
        projects.values().forEach(stashProject -> {
            stashProject.repos.values().forEach(stashRepo -> {
                stashRepo.branches.values().forEach(stashBranch -> {
                    source.getProjectByKey(stashProject.key).getRepoBySlug(stashRepo.slug).branches
                            .putIfAbsent(stashBranch.getId(), stashBranch);
                });
            });
        });
    }

    /**
     * Delete those projects, repos and branches, that don't appear in given projects, from the source
     * <p>
     * Useful for updating source, when something on remote source may have been deleted since last update
     *
     * @param source   origin from which projects/repos/branches will be removed if necessary
     * @param projects list of projects/repos/branches that are allowed to stay in source after update
     */
    public static void constrainSourceWithGivenProjectsData(@Nonnull Source source, Map<String, Project> projects) {
        source.projects.values().removeIf(project -> !projects.containsKey(project.key));
        source.projects.values().forEach(project -> project.repos.values()
                .removeIf(repo -> !projects.get(project.key).repos.containsKey(repo.slug)));
        source.projects.values()
                .forEach(project -> project.repos.values()
                        .forEach(repo -> repo.branches.values()
                                .removeIf(branch -> !projects.get(project.key).repos.get(repo.slug).branches
                                        .containsKey(branch.getId()))));
    }

    /**
     * Returns settings filename from properties. If absent, default filename is returned.
     *
     * @return Settings filename from properties or default if absent
     */
    public static String getSettingsFilenameFromProperties() {

        final String defaultFilename = "transformerSettings.ser";
        PropertyHandler propertyHandler;
        try {
            propertyHandler = PropertyHandler.getInstance();
        } catch (IOException e) {
            log.error("Couldn't load the properties file", e);
            return defaultFilename;
        }
        return propertyHandler.getValue("settings.filename", defaultFilename);
    }

    /**
     * Determines and sets all verification flags on a given source
     * <p>
     * <ul>
     * <li>Name should be unique and nonempty to be correct</li>
     * <li>Function will try to connect and verify with source, setting exist and credentialsCorrect flags accordingly</li>
     * <li><b>Source type can only be checked if credentials are correct. Otherwise it will always be false</b></li>
     * </ul>
     *  @param source          will be checked for all conditions and proper flags will be set on it
     *  @param existingSources used to check whether source name is unique
     *  @return whether source is correct after all checks
     */
    public static boolean verifySource(Source source, List<Source> existingSources) {
        source.setSourceExists(false);
        source.setCredentialsCorrect(false);
        source.setNameCorrect(false);
        source.setSourceTypeCorrect(false);

        // is name unique and not empty
        if (!Strings.isNullOrEmpty(source.getName()) && !existingSources.stream()
                .anyMatch(s -> s.getName().equals(source.getName()) && s.getId() != source.getId())) {
            source.setNameCorrect(true);
        }

        try {
            // check for connection data correctness
            StashBitbucketClient stashBitbucketClient = ClientFactory.getConfiguredStashBitbucketClient(source);
            if (stashBitbucketClient.isVerified()) {
                source.setSourceExists(true);
                source.setCredentialsCorrect(true);
            } else if (stashBitbucketClient.doesExist()) {
                source.setSourceExists(true);
            }

			/*
             * check if sourceType matches what lays at the end of given url
			 * link has to be verified user to get appProperties on bitbucket
			 * (but not on stash)
			 */
            if (source.isCredentialsCorrect() && Objects.nonNull(source.getSourceType())) {
                Boolean isSourceTypeRight = stashBitbucketClient.getApplicationProperties()
                        .map(ap -> ap.getDisplayName().equalsIgnoreCase(source.getSourceType().toString()))
                        .orElse(false);
                source.setSourceTypeCorrect(isSourceTypeRight);
            }
        } catch (MalformedURLException ignored) {
        }
        return source.isCorrect();
    }

    /**
     * Searches for propper password for the source in existing sources and sets it on the source
     *
     * @param source will have it source field set to correct password if possible. Otherwise it will be null
     * @param existingSources sources in which the proper password should be searched for (by source id)
     */
    public static void setCorrectPasswordForSource(Source source, List<Source> existingSources) {
        String previousPassword = existingSources.stream()
                .filter(previousSources -> previousSources.getId() == source.getId()).map(Source::getPassword)
                .findFirst().orElse(null);
        source.setPassword(previousPassword);
    }
}
