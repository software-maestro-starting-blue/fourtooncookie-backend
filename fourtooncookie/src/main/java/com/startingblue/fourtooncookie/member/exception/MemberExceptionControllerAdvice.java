package com.startingblue.fourtooncookie.member.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("com.startingblue.fourtooncookie.member")
public class MemberExceptionControllerAdvice {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundExceptionException(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member not found: " + e.getMessage());
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<String> handleEntityExistsExceptionException(EntityExistsException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member Exists: " + e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleEntityIllegalArgumentExceptionException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member IllegalArgumentException " + e.getMessage());
    }
}
