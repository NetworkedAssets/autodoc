package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;

import com.networkedassets.autodoc.transformer.manageSettings.require.ProjectsProvider;
import com.networkedassets.autodoc.transformer.settings.Source;

import java.net.MalformedURLException;


public class ProjectsProviderFactory {
    public static ProjectsProvider getInstance(Source source) throws MalformedURLException {
        ProjectsProvider projectsProvider = null;

        switch (source.getSourceType()) {
            //fallthrough!
            case STASH:
            case BITBUCKET:
                projectsProvider = new AtlassianProjectsProvider(source);
                break;
        }
        return projectsProvider;
    }
}
