package com.networkedassets.autodoc.transformer.usecases;

import com.networkedassets.autodoc.transformer.JavaDocGenerator;
import com.networkedassets.autodoc.transformer.PlantUmlGenerator;
import com.networkedassets.autodoc.transformer.infrastucture.javadoc.JavadocException;
import com.networkedassets.autodoc.transformer.settings.SettingsForSpace;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.Event;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

public class GenerateDocumentationFromProjectChangeCommand implements Command {
    @SuppressWarnings("unused")
    public static Logger log = LoggerFactory.getLogger(GenerateDocumentationFromProjectChangeCommand.class);

    private CreateOrUpdateSettings settingsManager;
    private JavaDocGenerator javaDocGenerator;
    private PlantUmlGenerator plantUmlGenerator;
    private Event projectChangeEvent;

    @Inject
    public GenerateDocumentationFromProjectChangeCommand(CreateOrUpdateSettings settingsManager,
                                                         JavaDocGenerator javaDocGenerator,
                                                         PlantUmlGenerator plantUmlGenerator,
                                                         Event projectChangeEvent) {
        this.settingsManager = settingsManager;
        this.javaDocGenerator = javaDocGenerator;
        this.plantUmlGenerator = plantUmlGenerator;
        this.projectChangeEvent = projectChangeEvent;
    }

    @Override
    public void execute() {
        String projectKey = projectChangeEvent.getProjectKey();
        String repoSlug = projectChangeEvent.getRepositorySlug();
        String branchId = projectChangeEvent.getBranchId();

        Collection<SettingsForSpace> interestedSpaces = settingsManager.getSettingsForSpaces().stream()
                .filter(s -> s.getProjectByKey(projectKey).getRepoBySlug(repoSlug).getBranchById(branchId).isListened)
                .collect(Collectors.toList());
        try {
            javaDocGenerator.generateFromStashAndPost(projectKey, repoSlug, branchId, interestedSpaces);
        } catch (JavadocException | IOException e) {
            e.printStackTrace();
        }

        try {
            plantUmlGenerator.generateFromStashAndPost(projectKey, repoSlug, branchId, interestedSpaces);
        } catch (IOException | JavadocException e) {
            e.printStackTrace();
        }

    }
}
