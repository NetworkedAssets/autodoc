package com.networkedassets.autodoc.transformer.usecases;

import static org.quartz.JobBuilder.newJob;

import javax.inject.Inject;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;

import com.networkedassets.autodoc.transformer.infrastucture.schedule.GenerationJob;

/**
 * Registers scheduled actions and invokes them when needed
 */
public class CreateOrUpdateSchedule {

    private final Scheduler scheduler;

    @Inject
    CreateOrUpdateSettings settingsManager;

    JobDetail generationJob;

    public CreateOrUpdateSchedule() throws SchedulerException {
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
