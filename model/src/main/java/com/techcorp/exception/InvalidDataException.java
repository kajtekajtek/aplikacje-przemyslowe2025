package com.techcorp.exception;

public class InvalidDataException extends Exception {
    private int line;

    public InvalidDataException(int line, String message) { 
        super(message); 
        this.line = line;
    }

    public int getLine() { return line; }

}
