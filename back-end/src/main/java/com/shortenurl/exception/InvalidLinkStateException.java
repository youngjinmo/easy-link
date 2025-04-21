package com.shortenurl.exception;

import org.springframework.http.HttpStatus;

public class InvalidLinkStateException extends CustomException {
    public InvalidLinkStateException() {
        super(HttpStatus.BAD_REQUEST, "Invalid link state");
    }
}
