package com.networkedassets.autodoc.transformer.infrastucture.di;

import javax.inject.Inject;

import com.networkedassets.autodoc.transformer.usecases.GenerateDocumentationFromProjectChangeCommand;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.Command;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.DocGeneratorFactory;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.DocSenderFactory;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.Event;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.ProcessEventCommandFactory;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.SettingsProvider;

public class EventDispatcher implements ProcessEventCommandFactory {
	private SettingsProvider settingsProvider;
	private DocGeneratorFactory docGeneratorFactory;
	private DocSenderFactory docSenderFactory;

	@Inject
	public EventDispatcher(SettingsProvider settingsProvider, DocGeneratorFactory docGeneratorFactory,
			DocSenderFactory docSenderFactory) {
		this.settingsProvider = settingsProvider;
		this.docGeneratorFactory = docGeneratorFactory;
		this.docSenderFactory = docSenderFactory;
	}

	@Override
	public Command createProcessEventCommand(Event event) {
		return dispatch(event);
	}

	/**
	 * WARNING: This method returns a "do nothing" Command if an inappropriate
	 * event was given
	 */
	public Command dispatch(Event event) {
		if (isProjectChangeEvent(event)) {
			return new GenerateDocumentationFromProjectChangeCommand(settingsProvider, docGeneratorFactory,
					docSenderFactory, event);
		} else {
			return DO_NOTHING;
		}
	}

	private boolean isProjectChangeEvent(Event event) {
		return event != null && event.isValidProjectChangeEvent();
	}
}
