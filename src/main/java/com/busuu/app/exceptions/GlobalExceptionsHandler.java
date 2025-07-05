package com.busuu.app.exceptions;


import com.busuu.app.dtos.responses.Response;
import com.busuu.app.utils.LocalizationUtils;
import com.busuu.app.utils.MessagesKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionsHandler
{
    private final LocalizationUtils localizationUtils;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleValidationExceptions(MethodArgumentNotValidException e)
    {
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : e.getBindingResult().getFieldErrors())
        {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        String errorMessage = errors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("; "));

        //Log error
        log.error(localizationUtils.getLocalizedMessage(MessagesKey.INVALID_ERROR, errorMessage));

        // Return response
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Response.builder()
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.INVALID_ERROR, errorMessage))
                        .status(HttpStatus.BAD_REQUEST.value())
                        .build()
        );
    }

    @ExceptionHandler(value = DataNotFoundException.class)
    ResponseEntity<Response> handlingDataNotFoundException(DataNotFoundException exception)
    {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Response.builder()
                        .message(exception.getMessage())
                        .status(HttpStatus.NOT_FOUND.value())
                        .build()
        );
    }

    @ExceptionHandler(value = ErrorHandleException.class)
    ResponseEntity<Response> handlingErrorHandleException(ErrorHandleException exception)
    {
        return ResponseEntity.status(exception.getStatusCode()).body(
                Response.builder()
                        .message(exception.getMessage())
                        .status(exception.getStatusCode().value())
                        .build()
        );
    }

    @ExceptionHandler(value = ExistDataException.class)
    ResponseEntity<Response> handlingExistDataException(ExistDataException exception)
    {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Response.builder()
                        .message(exception.getMessage())
                        .status(HttpStatus.CONFLICT.value())
                        .build()
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Response> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String message = "HTTP method '" + ex.getMethod() + "' is not supported for this endpoint";
        log.warn(message);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
                Response.builder()
                        .message(message)
                        .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                        .build()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response> handleInvalidJson(HttpMessageNotReadableException ex) {
        String message = "Malformed JSON request";
        log.warn(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Response.builder()
                        .message(message)
                        .status(HttpStatus.BAD_REQUEST.value())
                        .build()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response> handleAccessDeniedException(AccessDeniedException ex) {
        String message = "You do not have permission to perform this action";
        log.warn(message);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                Response.builder()
                        .message(message)
                        .status(HttpStatus.FORBIDDEN.value())
                        .build()
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Response> handleAuthenticationException(AuthenticationException ex) {
        String message = "Authentication failed: " + ex.getMessage();
        log.warn(message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Response.builder()
                        .message(message)
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleGeneralException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Response.builder()
                        .message("An unexpected error occurred. Please try again later.")
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .build()
        );
    }

    @ExceptionHandler(ExpiredTokenException.class)
    public ResponseEntity<Response> handleExpiredTokenException(ExpiredTokenException ex) {
        log.warn("Token expired: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Response.builder()
                        .message(ex.getMessage())
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .build()
        );
    }

    @ExceptionHandler(PermissionDenyException.class)
    public ResponseEntity<Response> handlePermissionDenyException(PermissionDenyException ex) {
        log.warn("Permission denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                Response.builder()
                        .message(ex.getMessage())
                        .status(HttpStatus.FORBIDDEN.value())
                        .build()
        );
    }

}