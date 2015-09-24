package com.networkedassets.autodoc.transformer.settings;

/**
 * Exception used in settings-api module
 */
public class SettingsException extends Exception {
    public SettingsException() {
        super();
    }

    public SettingsException(String message) {
        super(message);
    }

    public SettingsException(String message, Throwable cause) {
        super(message, cause);
    }

    public SettingsException(Throwable cause) {
        super(cause);
    }
}
