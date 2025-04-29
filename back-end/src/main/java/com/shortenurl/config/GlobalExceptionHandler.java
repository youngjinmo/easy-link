package com.shortenurl.config;

import com.shortenurl.exception.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<String> handleCustomException(CustomException e) {
        return ResponseEntity.status(e.getStatus()).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnExceptedException(Exception e) {
        return ResponseEntity.status(500).body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> illegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> illegalStateException(IllegalStateException e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }

    @ExceptionHandler(InvalidLinkStateException.class)
    public ResponseEntity<String> invalidLinkStateException(InvalidLinkStateException e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }

    @ExceptionHandler(LinkNotFoundException.class)
    public ResponseEntity<String> linkNotFoundException(LinkNotFoundException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

    @ExceptionHandler(LinkExpiredException.class)
    public ResponseEntity<String> linkExpiredException(LinkExpiredException e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }

    @ExceptionHandler(ExceedFreeLinkLimitException.class)
    public ResponseEntity<String> exceedFreeLinkLimitException(ExceedFreeLinkLimitException e) {
        return ResponseEntity.status(403).body(e.getMessage());
    }

    @ExceptionHandler(DuplicateLinkPathException.class)
    public ResponseEntity<String> duplicateLinkPathException(DuplicateLinkPathException e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }
}
