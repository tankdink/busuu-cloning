package com.busuu.app.exceptions;

import org.springframework.http.HttpStatus;

public class ErrorHandleException extends RuntimeException {
    private HttpStatus statusCode;
    private String errorCode;
    private String requestId;
    private Object requestBody = null;

    public ErrorHandleException(String message, HttpStatus statusCode, String errorCode, String requestId, Object requestBody) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.requestId = requestId;
        this.requestBody = requestBody;
    }

    public ErrorHandleException(String message, HttpStatus statusCode, String errorCode, String requestId) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.requestId = requestId;
    }

    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public HttpStatus getStatusCode() {
        return this.statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getRequestId() {
        return requestId;
    }
}

