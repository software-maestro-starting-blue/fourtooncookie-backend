package com.startingblue.fourtooncookie.diary.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DiaryLambdaInvocationException extends RuntimeException {

    public DiaryLambdaInvocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
