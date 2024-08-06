package com.startingblue.fourtooncookie.llm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class LLMExceptionControllerAdvice {

    @ExceptionHandler(LLMException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleLLMException(LLMException e) {
        log.error(e.getMessage(), e);
        return "Unexpected error occurred";
    }
}
