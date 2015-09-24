package com.networkedassets.autodoc.transformer;

import com.google.common.collect.ImmutableList;
import com.networkedassets.autodoc.transformer.settings.*;

import java.time.Instant;
import java.time.Period;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Handles the settings of the application
 */
public class SettingsManager {
    private Settings settings = new Settings();

    public SettingsManager() {
        //load settings from file if exist
        updateSettings();
    }

    public SettingsForSpace getSettingsForSpace(String spaceKey, String confluenceUrl) {
        SettingsForSpace settingsForSpace;
        try{
            settingsForSpace = settings.getSettingsForSpaces().stream().filter(s ->
                    (s.getSpaceKey().equals(spaceKey) && s.getConfluenceUrl().equals(confluenceUrl))).collect(Collectors.toList()).get(0);
        }catch(IndexOutOfBoundsException e){
            settingsForSpace = getDefaultSettingsForSpace(spaceKey, confluenceUrl);
        }
        return settingsForSpace;
    }

    public void setSettingsForSpace(SettingsForSpace settingsForSpace, String spaceKey, String confluenceUrl) {
        settings.getSettingsForSpaces()
                .removeIf(s -> (s.getSpaceKey().equals(spaceKey) && s.getConfluenceUrl().equals(confluenceUrl)));
        settings.getSettingsForSpaces().add(settingsForSpace);
    }

    private void updateSettings() {
        for (final ListIterator<SettingsForSpace> i = settings.getSettingsForSpaces().listIterator(); i.hasNext();){
            SettingsForSpace settingsForSpace = i.next();
            settingsForSpace = updateProjectsFromStash(settingsForSpace);
            i.set(settingsForSpace);
        }
    }

    private SettingsForSpace getDefaultSettingsForSpace(){
        SettingsForSpace defaultSettingsForSpace = new SettingsForSpace();
        defaultSettingsForSpace = updateProjectsFromStash(defaultSettingsForSpace);
        return defaultSettingsForSpace;
    }

    private SettingsForSpace getDefaultSettingsForSpace(String spaceKey, String confluenceUrl){
        SettingsForSpace defaultSettingsForSpace = getDefaultSettingsForSpace();
        defaultSettingsForSpace.setConfluenceUrl(confluenceUrl);
        defaultSettingsForSpace.setSpaceKey(spaceKey);
        return defaultSettingsForSpace;
    }

    private SettingsForSpace updateProjectsFromStash(SettingsForSpace settingsForSpace){
        SettingsForSpace updatedSettings = settingsForSpace;
        //TODO add projects, repos and branches from Stash, remove nonexistent
        return updatedSettings;
    }
}
