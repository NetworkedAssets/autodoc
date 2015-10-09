package com.networkedassets.autodoc.transformer;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.networkedassets.autodoc.transformer.event.Change;
import com.networkedassets.autodoc.transformer.event.Event;
import com.networkedassets.autodoc.transformer.javadoc.JavadocException;
import com.networkedassets.autodoc.transformer.settings.SettingsForSpace;

/**
 * Handles incoming events
 */
public class EventHandler {

	public static Logger log = LoggerFactory.getLogger(EventHandler.class);
	@Inject
	private SettingsManager settingsManager;
	@Inject
	private JavaDocGenerator javaDocGenerator;
	@Inject
	private PlantUmlGenerator plantUmlGenerator;
	@Inject
	private TaskExecutor taskExecutor;

	public EventHandler(SettingsManager settingsManager, JavaDocGenerator javaDocGenerator,
			PlantUmlGenerator plantUmlGenerator, TaskExecutor taskExecutor) {
		this.settingsManager = settingsManager;
		this.javaDocGenerator = javaDocGenerator;
		this.plantUmlGenerator = plantUmlGenerator;
		this.taskExecutor = taskExecutor;
	}

	public void handleEvent(Event event) throws IOException, JavadocException {

		String projectKey = event.getProjectKey();
		String repoSlug = event.getRepositorySlug();

		for (Change change : event.getChanges()) {
			if (!change.getType().equals("DELETE")) {
				Collection<SettingsForSpace> interestedSpaces = settingsManager
						.getSettingsForSpaces().stream().filter(s -> s.getProjectByKey(projectKey)
								.getRepoBySlug(repoSlug).getBranchById(change.getRefId()).isListened)
						.collect(Collectors.toList());
				javaDocGenerator.generateFromStashAndPost(projectKey, repoSlug, change.getRefId(), interestedSpaces);
				plantUmlGenerator.generateFromStashAndPost(projectKey, repoSlug, change.getRefId(), interestedSpaces);
				/*
				 * taskExecutor.add(new Runnable() { public void run() { try {
				 * javaDocGenerator.generateFromStashAndPost(projectKey,
				 * repoSlug, change.getRefId(), interestedSpaces); } catch
				 * (IOException e) { log.error("General IO exceptions", e); }
				 * catch (JavadocException e) { log.error(
				 * "Could not generate javaDoc", e); } } });
				 * 
				 * taskExecutor.add(new Runnable() { public void run() { try {
				 * plantUmlGenerator.generateFromStashAndPost(projectKey,
				 * repoSlug, change.getRefId(), interestedSpaces); } catch
				 * (IOException e) { log.error("General IO exceptions", e); }
				 * catch (JavadocException e) { log.error(
				 * "Could not generate plantUml", e); } } });
				 */
			}
		}
		/*
		 * try { taskExecutor.execute(); } catch (InterruptedException e) {
		 * log.error("Could not execute task", e); }
		 */
	}
}
