package com.busuu.app.exceptions;


public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException (String message) {
        super(message);
    }
}
