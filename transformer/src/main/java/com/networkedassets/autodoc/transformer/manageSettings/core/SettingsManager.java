package com.networkedassets.autodoc.transformer.manageSettings.core;

import com.networkedassets.autodoc.transformer.manageSettings.infrastructure.StashHookActivator;
import com.networkedassets.autodoc.transformer.manageSettings.infrastructure.StashProjectsProvider;
import com.networkedassets.autodoc.transformer.manageSettings.provide.in.SettingsSaver;
import com.networkedassets.autodoc.transformer.manageSettings.provide.out.SettingsProvider;
import com.networkedassets.autodoc.transformer.manageSettings.require.HookActivator;
import com.networkedassets.autodoc.transformer.manageSettings.require.ProjectsProvider;
import com.networkedassets.autodoc.transformer.settings.Branch;
import com.networkedassets.autodoc.transformer.settings.Project;
import com.networkedassets.autodoc.transformer.settings.Settings;
import com.networkedassets.autodoc.transformer.settings.SettingsForSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.*;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Handles the settings of the application
 */
public class SettingsManager implements SettingsProvider, SettingsSaver {

    public String settingsFilename;
    private Settings settings = new Settings();
    private static Logger log = LoggerFactory.getLogger(SettingsManager.class);
    private HookActivator hookActivator;
    private ProjectsProvider projectsProvider;

    public SettingsManager() {
        settingsFilename = getSettingsFilenameFromProperties();
        loadSettingsFromFile(settingsFilename);
        try {
            hookActivator = new StashHookActivator(settings.getTransformerSettings());
            projectsProvider = new StashProjectsProvider(settings.getTransformerSettings());
        } catch (MalformedURLException e) {
            log.error("Can't create stash client. Url in Transformer settings is malformed: ", e);
        }
        updateSettings();
    }

    private SettingsForSpace getDefaultSettingsForSpace() {
        SettingsForSpace defaultSettingsForSpace = new SettingsForSpace();
        updateProjectsFromStash(defaultSettingsForSpace);
        return defaultSettingsForSpace;
    }

    private SettingsForSpace getDefaultSettingsForSpace(String spaceKey, String confluenceUrl) {
        SettingsForSpace defaultSettingsForSpace = getDefaultSettingsForSpace();
        defaultSettingsForSpace.setConfluenceUrl(confluenceUrl);
        defaultSettingsForSpace.setSpaceKey(spaceKey);
        return defaultSettingsForSpace;
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
            updateProjectsFromStash(settingsForSpace);
            hookActivator.enableAllHooks(settingsForSpace.getProjectsMap());
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
            log.error("Can't save settings to {} - file was not found: ", settingsFile.getAbsolutePath(), e);
        } catch (IOException e) {
            log.error("Can't save settings to {} - general IO problem: ", settingsFile.getAbsolutePath(), e);
        }
    }

    private void loadSettingsFromFile(String filename) {
        File settingsFile = new File(filename);
        try (
                FileInputStream fileIn = new FileInputStream(settingsFile);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn)
        ) {
            settings = (Settings) objectIn.readObject();
            log.debug("Settings loaded from {}", settingsFile.getAbsolutePath());
        } catch (FileNotFoundException e) {
            log.error("Can't load settings from {} - file not found. Creating new default settings...", settingsFile.getAbsolutePath());
        } catch (ClassNotFoundException e) {
            log.error("Can't load settings from {} - serialization failed, class not found. Creating new default settings...", settingsFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Can't load settings from {}: ", settingsFile.getAbsolutePath(), e);
        }
    }

    private String getSettingsFilenameFromProperties() {

        final String defaultFilename = "transformerSettings.ser";

        InputStream propertiesStream = SettingsManager.class.getClassLoader().getResourceAsStream("autodoc_transformer.properties");
        Properties properties = new Properties();
        try {
            properties.load(propertiesStream);
        } catch (IOException e) {
            log.error("Couldn't load the configuration file", e);
            return defaultFilename;
        }
        return properties.getProperty("settings.filename", defaultFilename);
    }

    @SuppressWarnings("CodeBlock2Expr")
    private void updateProjectsFromStash(@Nonnull SettingsForSpace settingsForSpace) {
        //Get projects from stash
        Map<String, Project> stashProjects = projectsProvider.getStashProjects();

        //Delete projects, repos and branches in settings, that don't exist in stash (anymore)
        settingsForSpace.getProjects().removeIf(project -> !stashProjects.containsKey(project.key));
        settingsForSpace.getProjects().forEach(project ->
                project.repos.values().removeIf(repo ->
                        !stashProjects.get(project.key).repos.containsKey(repo.slug)));
        settingsForSpace.getProjects().forEach(project ->
                project.repos.values().forEach(repo ->
                        repo.branches.values().removeIf(branch ->
                                !stashProjects.get(project.key)
                                        .repos.get(repo.slug).branches.containsKey(branch.id))));


        //Add new branches, repos, and projects from stash
        stashProjects.values().forEach(stashProject -> {
            settingsForSpace.getProjectsMap().putIfAbsent(stashProject.key, stashProject);
        });
        stashProjects.values().forEach(stashProject -> {
            stashProject.repos.values().forEach(stashRepo -> {
                settingsForSpace.getProjectByKey(stashProject.key)
                        .repos.putIfAbsent(stashRepo.slug, stashRepo);
            });
        });
        stashProjects.values().forEach(stashProject -> {
            stashProject.repos.values().forEach(stashRepo -> {
                stashRepo.branches.values().forEach(stashBranch -> {
                    settingsForSpace.getProjectByKey(stashProject.key)
                            .getRepoBySlug(stashRepo.slug)
                            .branches.putIfAbsent(stashBranch.id, stashBranch);
                });
            });
        });

        //Update changed non-indexing data
        stashProjects.values().forEach(stashProject -> {
            Project project = settingsForSpace.getProjectByKey(stashProject.key);
            if (!project.name.equals(stashProject.name)) {
                project.name = stashProject.name;
            }
        });
        stashProjects.values().forEach(stashProject -> {
            stashProject.repos.values().forEach(stashRepo -> {
                stashRepo.branches.values().forEach(stashBranch -> {
                    Branch branch = settingsForSpace.getProjectByKey(stashProject.key)
                            .getRepoBySlug(stashRepo.slug)
                            .getBranchById(stashBranch.id);
                    if (!branch.displayId.equals(stashBranch.displayId)) {
                        branch.displayId = stashBranch.displayId;
                    }
                });
            });
        });


    }


}
