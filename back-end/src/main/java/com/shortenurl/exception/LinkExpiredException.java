package com.shortenurl.exception;

import org.springframework.http.HttpStatus;

public class LinkExpiredException extends CustomException{
    public LinkExpiredException() {
        super(HttpStatus.BAD_REQUEST, "Link has expired.");
    }
}
