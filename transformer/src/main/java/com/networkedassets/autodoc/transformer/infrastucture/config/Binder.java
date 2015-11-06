package com.networkedassets.autodoc.transformer.infrastucture.config;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.networkedassets.autodoc.transformer.JavaDocGenerator;
import com.networkedassets.autodoc.transformer.PlantUmlGenerator;
import com.networkedassets.autodoc.transformer.TaskExecutor;
import com.networkedassets.autodoc.transformer.usecases.PreprocessEvents;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.Event;
import com.networkedassets.autodoc.transformer.usecases.CreateOrUpdateSchedule;
import com.networkedassets.autodoc.transformer.usecases.CreateOrUpdateSettings;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Binder managing dependency injections in Jersey
 */
public class Binder extends AbstractBinder {

	Logger log = LoggerFactory.getLogger(Binder.class);

	@Override
	protected void configure() {
		JavaDocGenerator javaDocGenerator = new JavaDocGenerator();
		PlantUmlGenerator plantUmlGenerator = new PlantUmlGenerator();
		CreateOrUpdateSettings settingsManager = new CreateOrUpdateSettings();
		TaskExecutor taskExecutor = new TaskExecutor();
		Event requestModel=new Event();
		PreprocessEvents eventHandler = new PreprocessEvents(settingsManager, javaDocGenerator, plantUmlGenerator,
				taskExecutor,requestModel);

		CreateOrUpdateSchedule generationScheduler = null;
		try {
			generationScheduler = new CreateOrUpdateSchedule();
		} catch (SchedulerException e) {
			log.error("Cannot create scheduler: ", e);
		}

		bind(taskExecutor).to(TaskExecutor.class);
		bind(settingsManager).to(CreateOrUpdateSettings.class);
		bind(javaDocGenerator).to(JavaDocGenerator.class);
		bind(plantUmlGenerator).to(PlantUmlGenerator.class);
		bind(eventHandler).to(PreprocessEvents.class);
		bind(generationScheduler).to(CreateOrUpdateSchedule.class);
	}
}
