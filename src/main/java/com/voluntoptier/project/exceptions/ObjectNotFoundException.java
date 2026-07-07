package com.voluntoptier.project.exceptions;

public class ObjectNotFoundException extends Exception{
    public ObjectNotFoundException() {
        super("Invalid user input provided.");
    }

    public ObjectNotFoundException(String message) {
        super(message);
    }

    public ObjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectNotFoundException(Throwable cause) {
        super(cause);
    }
}
