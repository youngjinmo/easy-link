package com.shortenurl.exception;

import org.springframework.http.HttpStatus;

public class ExceedFreeLinkLimitException extends CustomException {
    public ExceedFreeLinkLimitException() {
        super(HttpStatus.FORBIDDEN, "You have exceeded the limit of free links.");
    }
}
