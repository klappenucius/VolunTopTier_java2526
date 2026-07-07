package com.voluntoptier.project.exceptions;
// za projekte kojima je start date > end date
// za worklogove koji su duži od trajanja projekta
// za datume rođenja koji su < 14 god
public class ImpossibleInputDataException extends Exception {

    public ImpossibleInputDataException() {
        super("Invalid user input provided.");
    }

    public ImpossibleInputDataException(String message) {
        super(message);
    }

    public ImpossibleInputDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImpossibleInputDataException(Throwable cause) {
        super(cause);
    }

}
