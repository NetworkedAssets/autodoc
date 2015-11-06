package com.networkedassets.autodoc.transformer.usecases;

import com.networkedassets.autodoc.transformer.JavaDocGenerator;
import com.networkedassets.autodoc.transformer.PlantUmlGenerator;
import com.networkedassets.autodoc.transformer.TaskExecutor;
import com.networkedassets.autodoc.transformer.infrastucture.javadoc.JavadocException;
import com.networkedassets.autodoc.transformer.settings.SettingsForSpace;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.Event;
import com.networkedassets.autodoc.transformer.usecases.boundary.require.Command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Handles incoming events
 */
public class PreprocessEvents implements Command {

	public static Logger log = LoggerFactory.getLogger(PreprocessEvents.class);

	private CreateOrUpdateSettings settingsManager;
	private JavaDocGenerator javaDocGenerator;
	private PlantUmlGenerator plantUmlGenerator;
	private TaskExecutor taskExecutor;
	private Event requestModel;

	@Inject
	public PreprocessEvents(CreateOrUpdateSettings settingsManager, JavaDocGenerator javaDocGenerator,
			PlantUmlGenerator plantUmlGenerator, TaskExecutor taskExecutor, Event requestModel) {
		this.settingsManager = settingsManager;
		this.javaDocGenerator = javaDocGenerator;
		this.plantUmlGenerator = plantUmlGenerator;
		this.taskExecutor = taskExecutor;
		this.requestModel = requestModel;
	}

	@Override
	public void execute() {
		String projectKey = requestModel.getProjectKey();
		String repoSlug = requestModel.getRepositorySlug();
		String branchId = requestModel.getBranchId();

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
