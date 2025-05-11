package com.shortenurl.exception;

import org.springframework.http.HttpStatus;

public class InternalServerException extends CustomException {
    public InternalServerException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }
    public InternalServerException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
