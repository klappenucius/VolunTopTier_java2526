package com.voluntoptier.project.exceptions;
// za neispravan email format
// za neispravan date format
// za empty or null id, name, last name, doB
public class InvalidInputFormatException extends Exception {

    public InvalidInputFormatException() {
        super("Invalid user input provided.");
    }

    public InvalidInputFormatException(String message) {
        super(message);
    }

    public InvalidInputFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidInputFormatException(Throwable cause) {
        super(cause);
    }

}
