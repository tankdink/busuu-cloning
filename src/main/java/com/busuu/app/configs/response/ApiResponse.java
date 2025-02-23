package com.busuu.app.configs.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApiResponse<T extends Serializable> {
    private T data;
    private String message;
    private String errorCode;
    private Integer statusCode;
    private String requestId;

    public ApiResponse(T data, String message, String errorCode, Integer statusCode, String requestId) {
        this.data = data;
        this.message = message;
        this.errorCode = errorCode;
        this.statusCode = statusCode;
        this.requestId = requestId;
    }

    public ApiResponse() {
    }

    public ApiResponse<T> success(T data, String message, Integer statusCode, String requestId) {
        return new ApiResponse<>(data, message, null, statusCode, requestId);
    }

    public ApiResponse<T> error(String message, String errorCode, Integer statusCode, String requestId) {
        return new ApiResponse<>(null, message, errorCode, statusCode, requestId);
    }

    public ApiResponse<T> error(T data, String message, String errorCode, Integer statusCode, String requestId) {
        return new ApiResponse<>(data, message, errorCode, statusCode, requestId);
    }
}
