package com.networkedassets.autodoc.transformer;

import com.google.common.collect.ImmutableList;
import com.networkedassets.autodoc.transformer.settings.*;

import java.time.Instant;
import java.time.Period;
import java.util.List;

public class TransformerServerMock implements TransformerServer {
    public static final SettingsForSpace settings = new SettingsForSpace() {
        @Override
        public List<Project> getProjects() {
            return getProjectsMocked();
        }

        @Override
        public void setProjects(List<Project> projects) {

        }
    };

    public TransformerServerMock(String address) {
    }

    private static ImmutableList<Project> getProjectsMocked() {
        return ImmutableList.<Project>builder()
                .add(new Project("Project the first", "PRJ1") {{
                    repos.put("R1", new Repo("Repo 1", "R1") {{
                        branches.put("R1/master", new Branch("master", "R1/master") {{
                            isListened=true;
                        }});
                        branches.put("R1/dev", new Branch("dev", "R1/dev") {{
                            scheduledEvents.add(new ScheduledEvent(Instant.now(), Period.ofDays(1)));
                        }});
                    }});

                    repos.put("R2", new Repo("Repo 2", "R2") {{
                        branches.put("R2/master", new Branch("master", "R2/master"));
                        branches.put("R2/dev", new Branch("dev", "R2/dev"));
                    }});
                }})
                .add(new Project("Project the second", "PRJ2") {{
                    repos.put("R11", new Repo("Repo 11", "R11") {{
                        branches.put("R11/master", new Branch("master", "R11/master") {{
                            isListened=true;
                        }});
                    }});
                }})
                .build();
    }

    @Override
    public SettingsForSpace getSettingsForSpace(String spaceKey) {
        return settings;
    }

    @Override
    public void saveSettingsForSpace(SettingsForSpace settings, String spaceKey) {

    }
}
