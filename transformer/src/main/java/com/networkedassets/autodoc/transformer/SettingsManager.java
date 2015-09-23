package com.networkedassets.autodoc.transformer;

import com.google.common.collect.ImmutableList;
import com.networkedassets.autodoc.transformer.settings.*;

import java.time.Instant;
import java.time.Period;

/**
 * Handles the settings of the application
 */
public class SettingsManager {
    private SettingsForSpace settingsForSpace;

    public SettingsManager() {
        settingsForSpace = new SettingsForSpace();
        settingsForSpace.setProjects(ImmutableList.<Project>builder()
                .add(new Project("Project the first", "PRJ1") {{
                    repos.add(new Repo("Repo 1", "R1") {{
                        branches.add(new Branch("master", "R1/master") {{
                            listenedEvents.put(GitEvent.PUSH, true);
                        }});
                        branches.add(new Branch("dev", "R1/dev") {{
                            scheduledEvents.add(new ScheduledEvent(Instant.now(), Period.ofDays(1)));
                        }});
                    }});

                    repos.add(new Repo("Repo 2", "R2") {{
                        branches.add(new Branch("master", "R2/master"));
                        branches.add(new Branch("dev", "R2/dev"));
                    }});
                }})
                .add(new Project("Project the second", "PRJ2") {{
                    repos.add(new Repo("Repo 11", "R11") {{
                        branches.add(new Branch("master", "R11/master") {{
                            listenedEvents.put(GitEvent.MERGE, true);
                        }});
                    }});
                }})
                .build());


    }

    public SettingsForSpace getSettingsForSpace() {
        return settingsForSpace;
    }

    public void setSettingsForSpace(SettingsForSpace settingsForSpace) {
        this.settingsForSpace = settingsForSpace;
    }
}
