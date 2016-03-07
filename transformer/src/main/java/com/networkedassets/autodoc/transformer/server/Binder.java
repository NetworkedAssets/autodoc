package com.networkedassets.autodoc.transformer.server;

import com.networkedassets.autodoc.transformer.handleRepoPush.core.DefaultDocumentationGeneratorFactory;
import com.networkedassets.autodoc.transformer.handleRepoPush.core.DocumentationFromCodeGenerator;
import com.networkedassets.autodoc.transformer.handleRepoPush.core.DocumentationGeneratorFactory;
import com.networkedassets.autodoc.transformer.handleRepoPush.infrastructure.ConfluenceDocumentationSender;
import com.networkedassets.autodoc.transformer.handleRepoPush.infrastructure.GitCodeProvider;
import com.networkedassets.autodoc.transformer.handleRepoPush.provide.in.PushEventProcessor;
import com.networkedassets.autodoc.transformer.handleRepoPush.require.CodeProvider;
import com.networkedassets.autodoc.transformer.handleRepoPush.require.DocumentationSender;
import com.networkedassets.autodoc.transformer.manageSettings.core.SettingsManager;
import com.networkedassets.autodoc.transformer.manageSettings.provide.in.*;
import com.networkedassets.autodoc.transformer.manageSettings.provide.out.SettingsProvider;
import com.networkedassets.autodoc.transformer.manageSettings.provide.out.SourceProvider;
import com.networkedassets.autodoc.transformer.util.PasswordStoreService;
import com.networkedassets.autodoc.transformer.util.PropertyHandler;
import com.networkedassets.autodoc.transformer.util.SettingsEncryptor;

import com.networkedassets.autodoc.transformer.util.SettingsPersistor;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Binder managing dependency injections in Jersey
 */
public class Binder extends AbstractBinder {

	Logger log = LoggerFactory.getLogger(Binder.class);

	@Override
	protected void configure() {
		DefaultDocumentationGeneratorFactory docFactory = new DefaultDocumentationGeneratorFactory();
		SchedulerFactory factory = new StdSchedulerFactory();

		Scheduler scheduler = null;
		try {
			scheduler = factory.getScheduler();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

		PasswordStoreService passwordService = new PasswordStoreService(
				PropertyHandler.getInstance().getValue("encrypt.password.filepath"));
		SettingsEncryptor settingsEncryptor = new SettingsEncryptor(
				passwordService.getProperty(PasswordStoreService.PropertyType.PASSWORD),
				passwordService.getProperty(PasswordStoreService.PropertyType.SALT));
		SettingsPersistor settingsPersistor = new SettingsPersistor(settingsEncryptor);

		SettingsManager settingsManager = new SettingsManager(scheduler, settingsPersistor);
		ConfluenceDocumentationSender sender = new ConfluenceDocumentationSender();
		GitCodeProvider codeProvider = new GitCodeProvider();
		DocumentationFromCodeGenerator docGen = new DocumentationFromCodeGenerator(settingsManager, docFactory, sender,
				codeProvider);

		bind(settingsEncryptor).to(SettingsEncryptor.class);
		bind(scheduler).to(Scheduler.class);
		bind(settingsManager).to(SettingsSaver.class);
		bind(settingsManager).to(SettingsProvider.class);
		bind(settingsManager).to(SourceProvider.class);
		bind(settingsManager).to(SourceCreator.class);
		bind(settingsManager).to(SourceRemover.class);
		bind(settingsManager).to(SourceModifier.class);
		bind(settingsManager).to(BranchModifier.class);
		bind(settingsManager).to(EventScheduler.class);
		bind(docFactory).to(DocumentationGeneratorFactory.class);
		bind(sender).to(DocumentationSender.class);
		bind(codeProvider).to(CodeProvider.class);
		bind(docGen).to(PushEventProcessor.class);
	}
}
