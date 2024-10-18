package com.startingblue.fourtooncookie.diary.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DiaryDuplicateException extends RuntimeException{
    public DiaryDuplicateException(String message) {
        super(message);
    }
}