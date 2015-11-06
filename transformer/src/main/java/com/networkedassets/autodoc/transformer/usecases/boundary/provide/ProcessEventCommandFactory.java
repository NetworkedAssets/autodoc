package com.networkedassets.autodoc.transformer.usecases.boundary.provide;

@FunctionalInterface
public interface ProcessEventCommandFactory {
    /**
     * This is here so one can check if the "do nothing" Command was returned from
     * {@link ProcessEventCommandFactory#createProcessEventCommand(Event)}
     */
    Command DO_NOTHING = () -> {};
    Command createProcessEventCommand(Event requestModel);
}
