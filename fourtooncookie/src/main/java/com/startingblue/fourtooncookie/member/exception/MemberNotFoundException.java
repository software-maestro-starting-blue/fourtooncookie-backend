package com.startingblue.fourtooncookie.member.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MemberNotFoundException extends NoSuchElementException {
    public MemberNotFoundException(String message) {
        super(message);
    }
}
