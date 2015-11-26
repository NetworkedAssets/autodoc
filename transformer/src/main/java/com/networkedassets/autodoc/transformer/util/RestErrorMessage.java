package com.networkedassets.autodoc.transformer.util;

/**
 * Created by kamil on 26.11.2015.
 */
public class RestErrorMessage {
    private int status;
    private String message;
    private String link;
    private String developerMessage;

    public RestErrorMessage() {
    }

    public RestErrorMessage(int status, String message, String link, String developerMessage) {
        this.status = status;
        this.message = message;
        this.link = link;
        this.developerMessage = developerMessage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = developerMessage;
    }
}
