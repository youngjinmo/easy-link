package com.shortenurl.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends CustomException {
    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, "User not found");
    }
}
