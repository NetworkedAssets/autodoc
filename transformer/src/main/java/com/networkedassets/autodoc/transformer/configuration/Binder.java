package com.networkedassets.autodoc.transformer.configuration;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.networkedassets.autodoc.transformer.EventHandler;
import com.networkedassets.autodoc.transformer.JavaDocGenerator;
import com.networkedassets.autodoc.transformer.PlantUmlGenerator;
import com.networkedassets.autodoc.transformer.Scheduler;
import com.networkedassets.autodoc.transformer.SettingsManager;
import com.networkedassets.autodoc.transformer.TestManager;

/**
 * Binder managing dependency injections in Jersey
 */
public class Binder extends AbstractBinder {

	@Override
	protected void configure() {
		JavaDocGenerator javaDocGenerator = new JavaDocGenerator();
		PlantUmlGenerator plantUmlGenerator = new PlantUmlGenerator();
		SettingsManager settingsManager = new SettingsManager();
		EventHandler eventHandler = new EventHandler(settingsManager, javaDocGenerator, plantUmlGenerator);

		bind(settingsManager).to(SettingsManager.class);
		bind(javaDocGenerator).to(JavaDocGenerator.class);
		bind(plantUmlGenerator).to(PlantUmlGenerator.class);
		bind(eventHandler).to(EventHandler.class);
		bind(new Scheduler()).to(Scheduler.class);
		bind(new TestManager()).to(TestManager.class);
	}
}
