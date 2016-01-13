package com.networkedassets.autodoc.transformer.server;

import com.networkedassets.autodoc.transformer.handleRepoPush.core.DocumentationFromCodeGenerator;
import com.networkedassets.autodoc.transformer.handleRepoPush.core.DocumentationGeneratorFactory;
import com.networkedassets.autodoc.transformer.handleRepoPush.infrastructure.ConfluenceDocumentationSender;
import com.networkedassets.autodoc.transformer.handleRepoPush.infrastructure.GitCodeProvider;
import com.networkedassets.autodoc.transformer.handleRepoPush.provide.in.PushEventProcessor;
import com.networkedassets.autodoc.transformer.handleRepoPush.require.DocumentationSender;
import com.networkedassets.autodoc.transformer.manageSettings.core.SettingsManager;
import com.networkedassets.autodoc.transformer.manageSettings.provide.in.*;
import com.networkedassets.autodoc.transformer.manageSettings.provide.out.SettingsProvider;
import com.networkedassets.autodoc.transformer.handleRepoPush.core.DefaultDocumentationGeneratorFactory;
import com.networkedassets.autodoc.transformer.handleRepoPush.require.CodeProvider;
import com.networkedassets.autodoc.transformer.manageSettings.provide.out.SourceProvider;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Binder managing dependency injections in Jersey
 */
public class Binder extends AbstractBinder {

	Logger log = LoggerFactory.getLogger(Binder.class);

	@Override
	protected void configure() {

		SettingsManager settingsManager = new SettingsManager();
		DefaultDocumentationGeneratorFactory docFactory = new DefaultDocumentationGeneratorFactory();
		ConfluenceDocumentationSender sender = new ConfluenceDocumentationSender();
		GitCodeProvider codeProvider = new GitCodeProvider();
		DocumentationFromCodeGenerator docGen = new DocumentationFromCodeGenerator(settingsManager, docFactory, sender,
				codeProvider);
		bind(settingsManager).to(SettingsSaver.class);
		bind(settingsManager).to(SettingsProvider.class);
		bind(settingsManager).to(SourceProvider.class);
		bind(settingsManager).to(SourceCreator.class);
		bind(settingsManager).to(SourceRemover.class);
		bind(settingsManager).to(SourceModifier.class);
		bind(settingsManager).to(BranchModifier.class);
		bind(docFactory).to(DocumentationGeneratorFactory.class);
		bind(sender).to(DocumentationSender.class);
		bind(codeProvider).to(CodeProvider.class);
		bind(docGen).to(PushEventProcessor.class);
	}
}
