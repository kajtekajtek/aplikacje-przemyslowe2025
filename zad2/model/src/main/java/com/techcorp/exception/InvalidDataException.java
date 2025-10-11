package com.techcorp.exception;

public class InvalidDataException extends Exception {
    private int line;

    public InvalidDataException(String message, int line) { 
        super(message); 
        this.line = line;
    }

    public int getLine() { return line; }

}
