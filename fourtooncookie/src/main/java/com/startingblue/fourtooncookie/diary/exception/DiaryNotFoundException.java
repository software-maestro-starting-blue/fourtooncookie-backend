package com.startingblue.fourtooncookie.diary.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DiaryNotFoundException extends NoSuchElementException {

    public DiaryNotFoundException() {
        super();
    }
}
