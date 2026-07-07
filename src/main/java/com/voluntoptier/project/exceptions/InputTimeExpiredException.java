package com.voluntoptier.project.exceptions;

public class InputTimeExpiredException extends RuntimeException{
    public InputTimeExpiredException() {
        super("User input timed out. No input received within the allowed time.");
    }

    public InputTimeExpiredException(String message) {
        super(message);
    }

    public InputTimeExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public InputTimeExpiredException(Throwable cause) {
        super(cause);
    }
}

