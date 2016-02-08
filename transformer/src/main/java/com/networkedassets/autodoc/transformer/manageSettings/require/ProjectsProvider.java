package com.networkedassets.autodoc.transformer.manageSettings.require;



import com.networkedassets.autodoc.transformer.settings.Project;

import java.util.Map;

public interface ProjectsProvider {

    Map<String, Project> getProjects();

}
