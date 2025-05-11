package com.shortenurl.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends CustomException {
    public BadRequestException() {
        super(HttpStatus.BAD_REQUEST, "Bad request");
    }
    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
