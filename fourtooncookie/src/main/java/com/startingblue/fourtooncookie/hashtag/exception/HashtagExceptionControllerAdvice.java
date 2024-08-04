package com.startingblue.fourtooncookie.hashtag.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("com.startingblue.fourtooncookie.hashtag")
public class HashtagExceptionControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(HashtagExceptionControllerAdvice.class);

    @ExceptionHandler(HashtagNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleHashtagNotFoundException(HashtagNotFoundException e) {
        log.error(e.getMessage());
        return "Hashtag not found";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException e) {
        log.error(e.getMessage());
        return "Bad Request";
    }
}
