package com.startingblue.fourtooncookie.member.authorization.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("com.startingblue.fourtooncookie.member.authorization")
public class MemberAuthorizationExceptionControllerAdvice {

    @ExceptionHandler(TokenNotFoundException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleTokenNotFound(TokenNotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleUnauthorizedAccess(UnauthorizedAccessException e) {
        return e.getMessage();
    }

    @ExceptionHandler(InvalidPathVariableException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleInvalidPathVariable(InvalidPathVariableException e) {
        return e.getMessage();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleException(Exception e) {
        return e.getMessage();
    }
}
