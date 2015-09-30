package com.networkedassets.autodoc.transformer.uml;


  @SuppressWarnings("serial")
public class PlantUMLException extends Exception {
    public PlantUMLException() {
    }

    public PlantUMLException(String message) {
        super(message);
    }

    public PlantUMLException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlantUMLException(Throwable cause) {
        super(cause);
    }
}

