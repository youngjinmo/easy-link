package com.shortenurl.exception;

import org.springframework.http.HttpStatus;

public class DuplicateLinkPathException extends CustomException {
    public DuplicateLinkPathException() {
        super(HttpStatus.BAD_REQUEST, "Duplicate link path");
    }
}
