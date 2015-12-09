package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;

import com.networkedassets.autodoc.transformer.manageSettings.require.ProjectsProvider;
import com.networkedassets.autodoc.transformer.settings.Source;

import java.net.MalformedURLException;

/**
 * Created by kamil on 03.12.2015.
 */
public class ProjectsProviderFactory {
    public static ProjectsProvider getInstance(Source source) throws MalformedURLException {
        ProjectsProvider projectsProvider = null;

        switch (source.getSourceType()) {
            case STASH:
                projectsProvider = new StashProjectsProvider(source);
                break;
            //not yet implemented
            case BITBUCKET:
                projectsProvider = new BitbucketProjectsProvider(source);
                break;
        }
        return projectsProvider;
    }
}
