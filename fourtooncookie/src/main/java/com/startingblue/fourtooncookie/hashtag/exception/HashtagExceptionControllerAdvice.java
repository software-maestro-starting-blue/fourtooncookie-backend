package com.startingblue.fourtooncookie.hashtag.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("com.startingblue.fourtooncookie.hashtag")
public class HashtagExceptionControllerAdvice {

    @ExceptionHandler(HashtagNoSuchElementException.class)
    public ResponseEntity<?> handleHashtagNotFoundException(HashtagNoSuchElementException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HashtagTypeNotFoundException.class)
    public ResponseEntity<?> handleHashtagTypeNotFoundException(HashtagTypeNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
