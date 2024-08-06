package com.startingblue.fourtooncookie.hashtag.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("com.startingblue.fourtooncookie.hashtag")
@Slf4j
public class HashtagExceptionControllerAdvice {

    @ExceptionHandler(HashtagNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleHashtagNotFoundException(HashtagNotFoundException e) {
        log.error(e.getMessage(), e);
        return "Hashtag not found";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException e) {
        log.error(e.getMessage(), e);
        return "Bad Request";
    }
}
