package com.busuu.app.exceptions;

public class InvalidFileException extends RuntimeException
{
    public InvalidFileException(String message) {
        super(message);
    }
}
