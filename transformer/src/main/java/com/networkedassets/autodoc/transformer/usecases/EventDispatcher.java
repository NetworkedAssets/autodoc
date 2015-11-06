package com.networkedassets.autodoc.transformer.usecases;

import com.networkedassets.autodoc.transformer.JavaDocGenerator;
import com.networkedassets.autodoc.transformer.PlantUmlGenerator;
import com.networkedassets.autodoc.transformer.TaskExecutor;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.Event;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.Command;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.ProcessEventCommandFactory;

import javax.inject.Inject;

public class EventDispatcher implements ProcessEventCommandFactory {
    private CreateOrUpdateSettings settingsManager;
    private JavaDocGenerator javaDocGenerator;
    private PlantUmlGenerator plantUmlGenerator;
    private TaskExecutor taskExecutor;

    @Inject
    public EventDispatcher(CreateOrUpdateSettings settingsManager, JavaDocGenerator javaDocGenerator,
                           PlantUmlGenerator plantUmlGenerator, TaskExecutor taskExecutor) {
        this.settingsManager = settingsManager;
        this.javaDocGenerator = javaDocGenerator;
        this.plantUmlGenerator = plantUmlGenerator;
        this.taskExecutor = taskExecutor;
    }

    @Override
    public Command createProcessEventCommand(Event event) {
        return dispatch(event);
    }

    /**
     * WARNING: This method returns a "do nothing" Command if an inappropriate event was given
     */
    public Command dispatch(Event event) {
        if (isProjectChangeEvent(event)) {
            return new GenerateDocumentationFromProjectChangeCommand(settingsManager, javaDocGenerator,
                    plantUmlGenerator, event);
        } else {
            return DO_NOTHING;
        }
    }

    private boolean isProjectChangeEvent(Event event) {
        return event != null && event.isValidProjectChangeEvent();
    }
}
