package com.startingblue.fourtooncookie.member.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class MemberDuplicateException extends RuntimeException{
    public MemberDuplicateException(String message) {
        super(message);
    }
}
