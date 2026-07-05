package com.aziz.budgetpilotai.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<Map<String, Object>>
    handleValidation(
            MethodArgumentNotValidException exception
    ) {
        List<String> errors =
                exception
                        .getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(
                                DefaultMessageSourceResolvable
                                        ::getDefaultMessage
                        )
                        .toList();

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "timestamp",
                LocalDateTime.now()
        );

        response.put("status", 400);
        response.put(
                "message",
                "Données invalides"
        );

        response.put(
                "errors",
                errors
        );

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(
            ResponseStatusException.class
    )
    public ResponseEntity<Map<String, Object>>
    handleResponseStatus(
            ResponseStatusException exception
    ) {
        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "timestamp",
                LocalDateTime.now()
        );

        response.put(
                "status",
                exception
                        .getStatusCode()
                        .value()
        );

        response.put(
                "message",
                exception.getReason()
        );

        return ResponseEntity
                .status(
                        exception.getStatusCode()
                )
                .body(response);
    }
}