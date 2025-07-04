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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
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
        return ResponseEntity.badRequest().body(
                Response.builder()
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.INVALID_ERROR, errorMessage))
                        .status(HttpStatus.BAD_REQUEST.value())
                        .build()
        );
    }

    @ExceptionHandler(value = DataNotFoundException.class)
    ResponseEntity<Response> handlingDataNotFoundException(DataNotFoundException exception)
    {
        return ResponseEntity.badRequest().body(
                Response.builder()
                        .message(exception.getMessage())
                        .status(HttpStatus.NOT_FOUND.value())
                        .build()
        );

    }

    @ExceptionHandler(value = ErrorHandleException.class)
    ResponseEntity<Response> handlingErrorHandleException(ErrorHandleException exception)
    {
        return ResponseEntity.badRequest().body(
                Response.builder()
                        .message(exception.getMessage())
                        .status(exception.getStatusCode().value())
                        .build()
        );

    }

    @ExceptionHandler(value = ExistDataException.class)
    ResponseEntity<Response> handlingExistDataException(ExistDataException exception)
    {
        return ResponseEntity.badRequest().body(
                Response.builder()
                        .message(exception.getMessage())
                        .status(HttpStatus.CONFLICT.value())
                        .build()
        );

    }

}