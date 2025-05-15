package com.shortenurl.exception;

import org.springframework.http.HttpStatus;

public class TokenExpiredException extends CustomException {
    public TokenExpiredException() {
        super(HttpStatus.UNAUTHORIZED, "Token expired");
    }
}
