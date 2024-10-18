package com.startingblue.fourtooncookie.diary.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DiaryConversionException extends RuntimeException {
    public DiaryConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
