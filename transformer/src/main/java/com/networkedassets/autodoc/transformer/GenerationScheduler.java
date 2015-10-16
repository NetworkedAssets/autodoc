package com.networkedassets.autodoc.transformer;

import com.networkedassets.autodoc.transformer.settings.ScheduledEvent;
import org.quartz.*;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import javax.inject.Inject;

import static org.quartz.JobBuilder.*;

/**
 * Registers scheduled actions and invokes them when needed
 */
public class GenerationScheduler {

    private final Scheduler scheduler;

    @Inject
    SettingsManager settingsManager;

    JobDetail generationJob;

    public GenerationScheduler() throws SchedulerException {
        SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();

        scheduler = schedFact.getScheduler();
        scheduler.start();

        generationJob = newJob(GenerationJob.class)
                .withIdentity("generationJob", "generationGroup")
                .build();
    }



    public void updateSchedule() {
        settingsManager.getSettingsForSpaces().stream().forEach(settingsForSpace -> {
            settingsForSpace.getProjects().forEach(project -> {
                project.repos.values().stream().forEach(repo -> {
                    repo.branches.values().stream().forEach(branch -> {
                        branch.scheduledEvents.stream().forEach(scheduledEvent -> {

                        });
                    });
                });
            });
        });
    }

//    private Trigger generateTriggerFromScheduledEvent(ScheduledEvent scheduledEvent){
//
//    }
}
