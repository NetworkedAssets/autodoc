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
import com.networkedassets.autodoc.transformer.settings.Source;
import com.networkedassets.autodoc.transformer.util.PropertyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.*;
import java.net.MalformedURLException;
import java.util.Map;

/**
 * Handles the settings of the application
 */
public class SettingsManager implements SettingsProvider, SettingsSaver {

    public String settingsFilename;

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    private Settings settings = new Settings();
    private static Logger log = LoggerFactory.getLogger(SettingsManager.class);
    public static boolean LOLIDONTEVENRUNONCE = false;

    public SettingsManager() {
        settingsFilename = getSettingsFilenameFromProperties();
        loadSettingsFromFile(settingsFilename);
        updateSettings();
    }




    @Override
    public Settings getCurrentSettings() {
        if(!LOLIDONTEVENRUNONCE){
            Source source = new Source();
            settings.getSources().add(source);
            LOLIDONTEVENRUNONCE = true;
        }
        updateSettings();
        return settings;
    }

    @Override
    public void setCurrentSettings(Settings settings) {
        //TODO update those settings with them clients
        //TODO look for null passwords and if they appear - preserve old ones!
        this.settings = settings;
        saveSettingsToFile(settingsFilename);
    }

    private void updateSettings() {
        settings.getSources().forEach(source -> {
            try {
                updateProjectsFromSource(source);
                HookActivator hookActivator;
                switch (source.getSourceType()){
                    case STASH:
                        hookActivator = new StashHookActivator(source, settings.getTransformerSettings().getLocalhostAddress());
                        break;
                    default:
                        log.error("Trying to update projects from unimplemented source!");
                        return;
                }
                hookActivator.enableAllHooks();
            } catch (MalformedURLException e) {
                log.error("Source {} has malformed URL. Can't load projects: ", source.toString(), e);
            }
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
        PropertyHandler propertyHandler;
        try {
            propertyHandler = PropertyHandler.getInstance();
        } catch (IOException e) {
            log.error("Couldn't load the properties file", e);
            return defaultFilename;
        }
        return propertyHandler.getValue("settings.filename", defaultFilename);
    }


    @SuppressWarnings("CodeBlock2Expr")
    private void updateProjectsFromSource(@Nonnull Source source) throws MalformedURLException {
        //Get projects from source
        ProjectsProvider projectsProvider;
        switch (source.getSourceType()) {
            case STASH:
                projectsProvider = new StashProjectsProvider(source);
                break;
            //not yet implemented
            case BITBUCKET:
            case GITHUB:
            default:
                log.error("Trying to update projects from unimplemented source!");
                return;
        }

        Map<String, Project> sourceProjects = projectsProvider.getProjects();

        //Delete projects, repos and branches in settings, that don't exist in stash (anymore)
        source.projects.values().removeIf(project -> !sourceProjects.containsKey(project.key));
        source.projects.values().forEach(project ->
                project.repos.values().removeIf(repo ->
                        !sourceProjects.get(project.key).repos.containsKey(repo.slug)));
        source.projects.values().forEach(project ->
                project.repos.values().forEach(repo ->
                        repo.branches.values().removeIf(branch ->
                                !sourceProjects.get(project.key)
                                        .repos.get(repo.slug).branches.containsKey(branch.id))));


        //Add new branches, repos, and projects from stash
        sourceProjects.values().forEach(stashProject -> {
            source.projects.putIfAbsent(stashProject.key, stashProject);
        });
        sourceProjects.values().forEach(stashProject -> {
            stashProject.repos.values().forEach(stashRepo -> {
                source.getProjectByKey(stashProject.key)
                        .repos.putIfAbsent(stashRepo.slug, stashRepo);
            });
        });
        sourceProjects.values().forEach(stashProject -> {
            stashProject.repos.values().forEach(stashRepo -> {
                stashRepo.branches.values().forEach(stashBranch -> {
                    source.getProjectByKey(stashProject.key)
                            .getRepoBySlug(stashRepo.slug)
                            .branches.putIfAbsent(stashBranch.id, stashBranch);
                });
            });
        });

        //Update changed non-indexing data
        sourceProjects.values().forEach(stashProject -> {
            Project project = source.getProjectByKey(stashProject.key);
            if (!project.name.equals(stashProject.name)) {
                project.name = stashProject.name;
            }
        });
        sourceProjects.values().forEach(stashProject -> {
            stashProject.repos.values().forEach(stashRepo -> {
                stashRepo.branches.values().forEach(stashBranch -> {
                    Branch branch = source.getProjectByKey(stashProject.key)
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
