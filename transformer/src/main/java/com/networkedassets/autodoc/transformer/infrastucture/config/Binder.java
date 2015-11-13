package com.networkedassets.autodoc.transformer.infrastucture.config;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.networkedassets.autodoc.transformer.infrastucture.di.DocGeneratorDispatcher;
import com.networkedassets.autodoc.transformer.infrastucture.di.EventDispatcher;
import com.networkedassets.autodoc.transformer.infrastucture.di.SenderDispatcher;
import com.networkedassets.autodoc.transformer.usecases.CreateOrUpdateSchedule;
import com.networkedassets.autodoc.transformer.usecases.CreateOrUpdateSettings;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.DocGeneratorFactory;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.DocSenderFactory;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.ProcessEventCommandFactory;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.SettingsProvider;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.SettingsSetter;

/**
 * Binder managing dependency injections in Jersey
 */
public class Binder extends AbstractBinder {

	Logger log = LoggerFactory.getLogger(Binder.class);

	@Override
	protected void configure() {

		CreateOrUpdateSettings settingsManager = new CreateOrUpdateSettings();
		SenderDispatcher senderDispatcher = new SenderDispatcher();
		DocGeneratorDispatcher docGeneratorDispatcher = new DocGeneratorDispatcher();
		EventDispatcher eventDispatcher = new EventDispatcher(settingsManager, docGeneratorDispatcher,
				senderDispatcher);

		CreateOrUpdateSchedule generationScheduler = null;
		try {
			generationScheduler = new CreateOrUpdateSchedule();
		} catch (SchedulerException e) {
			log.error("Cannot create scheduler: ", e);
		}

		bind(settingsManager).to(CreateOrUpdateSettings.class);
		bind(settingsManager).to(SettingsSetter.class);
		bind(settingsManager).to(SettingsProvider.class);
		bind(eventDispatcher).to(ProcessEventCommandFactory.class);
		bind(generationScheduler).to(CreateOrUpdateSchedule.class);
		bind(senderDispatcher).to(DocSenderFactory.class);
		bind(docGeneratorDispatcher).to(DocGeneratorFactory.class);
	}
}
