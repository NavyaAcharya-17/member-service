package com.surest.member_service.exception;

import org.springframework.http.HttpStatus;

public class MemberException extends Exception {

    private  HttpStatus httpStatus;

    public MemberException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
