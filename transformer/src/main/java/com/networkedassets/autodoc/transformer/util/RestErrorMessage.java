package com.networkedassets.autodoc.transformer.util;

/**
 * Created by kamil on 26.11.2015.
 */
public class RestErrorMessage {
    private String message;
    private String developerMessage;

    public RestErrorMessage() {
    }

    public RestErrorMessage(String message, String developerMessage) {
        this.message = message;
        this.developerMessage = developerMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = developerMessage;
    }
}
