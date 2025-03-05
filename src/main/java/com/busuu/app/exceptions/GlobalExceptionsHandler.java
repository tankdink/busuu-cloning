package com.busuu.app.exceptions;


import com.busuu.app.dtos.responses.Response;
import com.busuu.app.utils.LocalizationUtils;
import com.busuu.app.utils.MessagesKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//This is where handing all the exceptions
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionsHandler
{
    private final LocalizationUtils localizationUtils;

    // Handle DTOs jakartar exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleValidationExceptions(MethodArgumentNotValidException e)
    {
        //Create a map to store all the errors in 1 DTOs
        Map<String, String> errors = new HashMap<>();

        // Collect all validation errors to the just-created object
        for (FieldError error : e.getBindingResult().getFieldErrors())
        {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        // Convert the map to single string
        String errorMessage = errors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("; "));

        //Log error
        log.error(localizationUtils.getLocalizedMessage(MessagesKey.INVALID_ERROR, errorMessage));

        // Return response
        return ResponseEntity.badRequest().body(
                Response.builder()
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.INVALID_ERROR, errorMessage))
                        .status(HttpStatus.BAD_REQUEST)
                        .build()
        );
    }

    @ExceptionHandler(value = DataNotFoundException.class)
    ResponseEntity<Response> handlingDataNotFoundException(DataNotFoundException exception)
    {
        return ResponseEntity.badRequest().body(
                Response.builder()
                        .message(exception.getMessage())
                        .status(HttpStatus.BAD_REQUEST)
                        .build()
        );

    }

    @ExceptionHandler(value = ErrorHandleException.class)
    ResponseEntity<Response> handlingErrorHandleException(ErrorHandleException exception)
    {
        return ResponseEntity.badRequest().body(
                Response.builder()
                        .message(exception.getMessage())
                        .status(exception.getStatusCode())
                        .build()
        );

    }

    @ExceptionHandler(value = ExistDataException.class)
    ResponseEntity<Response> handlingExistDataException(ExistDataException exception)
    {
        return ResponseEntity.badRequest().body(
                Response.builder()
                        .message(exception.getMessage())
                        .status(HttpStatus.BAD_REQUEST)
                        .build()
        );

    }

}