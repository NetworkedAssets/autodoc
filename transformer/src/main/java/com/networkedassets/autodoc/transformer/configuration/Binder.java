package com.networkedassets.autodoc.transformer.configuration;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.networkedassets.autodoc.transformer.EventHandler;
import com.networkedassets.autodoc.transformer.JavaDocGenerator;
import com.networkedassets.autodoc.transformer.PlantUmlGenerator;
import com.networkedassets.autodoc.transformer.GenerationScheduler;
import com.networkedassets.autodoc.transformer.SettingsManager;
import com.networkedassets.autodoc.transformer.TestManager;
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
		SettingsManager settingsManager = new SettingsManager();
		EventHandler eventHandler = new EventHandler(settingsManager, javaDocGenerator, plantUmlGenerator);

		GenerationScheduler generationScheduler = null;
		try {
			generationScheduler = new GenerationScheduler();
		} catch (SchedulerException e) {
			log.error("Cannot create scheduler: ", e);
		}

		bind(settingsManager).to(SettingsManager.class);
		bind(javaDocGenerator).to(JavaDocGenerator.class);
		bind(plantUmlGenerator).to(PlantUmlGenerator.class);
		bind(eventHandler).to(EventHandler.class);
		bind(generationScheduler).to(GenerationScheduler.class);
		bind(new TestManager()).to(TestManager.class);
	}
}
