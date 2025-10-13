package com.surest.member_service.exception;

import org.springframework.http.HttpStatus;

public class MemberException extends Exception {
    private static final ErrorResponse errorResponse = ErrorResponse.builder().build();

    private final HttpStatus httpStatus;

    public MemberException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
        errorResponse.getErrors().add(message);
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
