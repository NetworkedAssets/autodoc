package com.networkedassets.autodoc.transformer.settings.view;

/**
 * Classes used for defining fields visibility when serializing/deserializing to JSON
 * Used just for annotations, so they should be empty
 */
public class Views {

    /**
     * Class used for defining fields visibility when serializing/deserializing to JSON
     */
    public interface BasicView {
    }

    public interface GetCredentialsView extends BasicView {
    }

    public interface SetCredentialsView extends GetCredentialsView {
    }

    public interface GetSettingsView extends GetCredentialsView {
    }

    public interface GetProjectsView extends GetCredentialsView, GetSettingsView {
    }

    public interface AddProjectReturnView extends GetProjectsView {

    }

    public interface AddProjectPasswordView extends AddProjectReturnView {

    }

    public interface InternalView extends AddProjectPasswordView, SetCredentialsView {

    }

}
