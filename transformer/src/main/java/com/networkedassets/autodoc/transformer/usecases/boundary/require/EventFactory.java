package com.networkedassets.autodoc.transformer.usecases.boundary.require;

import com.networkedassets.autodoc.transformer.usecases.boundary.provide.Event;

public interface EventFactory {

	Command createPreprocessEventsCommand(Event requestModel);

}
