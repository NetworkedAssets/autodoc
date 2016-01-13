package com.networkedassets.autodoc.transformer.util;

import com.networkedassets.autodoc.transformer.manageSettings.infrastructure.ProjectsProviderFactory;
import com.networkedassets.autodoc.transformer.manageSettings.require.ProjectsProvider;
import com.networkedassets.autodoc.transformer.settings.Branch;
import com.networkedassets.autodoc.transformer.settings.Project;
import com.networkedassets.autodoc.transformer.settings.Source;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.util.Map;

/**
 * Variable utils useful when dealing with transformer's settings
 */
public final class SettingsUtils {
    private SettingsUtils() {
    }

    public static void updateProjectsFromRemoteSource(@Nonnull Source source) throws MalformedURLException {
        // Get projects from source
        ProjectsProvider projectsProvider = ProjectsProviderFactory.getInstance(source);

        Map<String, Project> sourceProjects = projectsProvider.getProjects();
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
                .forEach(project -> {
                    project.repos.values().stream()
                            .filter(repo -> projects.get(project.key).repos.containsKey(repo.slug))
                            .forEach(repo -> repo.branches.values().stream()
                                    .filter(branch -> projects.get(project.key)
                                            .repos.get(repo.slug).branches.containsKey(branch.id))
                                    .forEach(branch -> branch.displayId = projects.get(project.key)
                                            .repos.get(repo.slug).branches.get(branch.id).displayId));
                });
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
                            .putIfAbsent(stashBranch.id, stashBranch);
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
                                        .containsKey(branch.id))));
    }
}
