package com.startingblue.fourtooncookie.character.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("com.startingblue.fourtooncookie.character")
public class CharacterExceptionControllerAdvice {

    @ExceptionHandler(CharacterNoSuchElementException.class)
    public ResponseEntity<String> handleCharacterNoSuchElementException(CharacterNoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(CharacterVisionTypeNoSuchElementException.class)
    public ResponseEntity<String> handleCharacterVisionTypeNoSuchElementExceptionNoSuchElementException(CharacterVisionTypeNoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
