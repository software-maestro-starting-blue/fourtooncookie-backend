package com.startingblue.fourtooncookie.member.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("com.startingblue.fourtooncookie.member")
public class MemberExceptionControllerAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentExceptionException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member not found: " + e.getMessage());
    }
}
