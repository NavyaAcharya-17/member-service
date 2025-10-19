package com.surest.member_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String STATUS = "status";
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";
    private static final String PATH = "path";
    private static final String TIMESTAMP = "timestamp";
    private static final String DETAILS = "details";

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleMemberException(MemberNotFoundException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put(STATUS, HttpStatus.NOT_FOUND.name());
        body.put(ERROR, "Resource Not Found");
        body.put(MESSAGE, "Member Not Found");
        body.put(PATH, request.getDescription(false).replace("uri=", ""));
        body.put(TIMESTAMP, LocalDateTime.now().format(formatter));

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR.name());
        body.put(ERROR, "Internal Server Error");
        body.put(MESSAGE, ex.getMessage());
        body.put(PATH, request.getDescription(false).replace("uri=", ""));
        body.put(TIMESTAMP, LocalDateTime.now().format(formatter));

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put(STATUS, HttpStatus.CONFLICT.name());
        body.put(ERROR, "Conflict");
        body.put(MESSAGE, "Resource already exists");
        body.put(PATH, request.getDescription(false).replace("uri=", ""));
        body.put(TIMESTAMP, LocalDateTime.now().format(formatter));

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> body = new HashMap<>();
        body.put(STATUS, HttpStatus.BAD_REQUEST.name());
        body.put(ERROR, "Validation Failed");
        body.put(MESSAGE, "Input validation failed");
        body.put(PATH, request.getDescription(false).replace("uri=", ""));
        body.put(TIMESTAMP, LocalDateTime.now().format(formatter));
        body.put(DETAILS, errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
