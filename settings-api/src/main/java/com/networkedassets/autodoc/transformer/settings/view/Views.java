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

    public interface GetSourcesView extends BasicView, SourceCorrectView {

    }

    public interface GetExpandedSourcesView extends GetCredentialsView, GetSourcesView {
    }

    public interface SourceCorrectView {

    }

    public interface AddSourceReturnView extends GetExpandedSourcesView, SourceCorrectView {

    }

    public interface AddSourcePasswordView extends AddSourceReturnView {

    }

    public interface InternalView extends AddSourcePasswordView, SetCredentialsView {

    }

}
