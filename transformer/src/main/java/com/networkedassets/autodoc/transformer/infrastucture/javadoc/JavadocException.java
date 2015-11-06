package com.networkedassets.autodoc.transformer.infrastucture.javadoc;

/**
 * Exception used in Javadoc wrapper
 */
@SuppressWarnings("serial")
public class JavadocException extends Exception {
    public JavadocException() {
    }

    public JavadocException(String message) {
        super(message);
    }

    public JavadocException(String message, Throwable cause) {
        super(message, cause);
    }

    public JavadocException(Throwable cause) {
        super(cause);
    }
}
