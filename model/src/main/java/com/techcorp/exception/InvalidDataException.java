package com.techcorp.exception;

public class InvalidDataException extends RuntimeException {
    private int line;

    public InvalidDataException(String message) {
        super(message);
        this.line = -1;
    }

    public InvalidDataException(int line, String message) {
        super(message);
        this.line = line;
    }

    public InvalidDataException(
        int       line, 
        String    message, 
        Throwable cause
    ) {
        super(message, cause);
        this.line = line;
    }

    public int getLine() { return line; }

}
