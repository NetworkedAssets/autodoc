package com.networkedassets.autodoc.transformer;

import com.networkedassets.autodoc.transformer.event.Event;
import com.networkedassets.autodoc.transformer.javadoc.JavadocException;
import com.networkedassets.autodoc.transformer.settings.SettingsForSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles incoming events
 */
public class EventHandler {

    public static Logger log = LoggerFactory.getLogger(EventHandler.class);
    @Inject
    private SettingsManager settingsManager;
    @Inject
    private JavaDocGenerator javaDocGenerator;

    public void handleEvent(Event event) throws IOException, JavadocException {
        String projectKey = event.getProjectKey();
        String repoSlug = event.getRepositorySlug();
        List<String> branchesIds = event.getChanges().stream().map(change -> change.getRefId()).collect(Collectors.toList());


        for (String branchId : branchesIds) {
            Collection<SettingsForSpace> interestedSpaces = settingsManager.getSettingsForSpaces().stream()
                    .filter(s -> s.getProjectByKey(projectKey).getRepoBySlug(repoSlug)
                            .getBranchById(branchId).isListened)
                    .collect(Collectors.toList());
            javaDocGenerator.generateFromStashAndPost(projectKey, repoSlug, branchId, interestedSpaces);
        }
    }
}
