package com.shortenurl.exception;

import org.springframework.http.HttpStatus;

public class LinkNotFoundException extends CustomException {
    public LinkNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Link not found");
    }
}
