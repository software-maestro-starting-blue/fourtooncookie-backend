package com.startingblue.fourtooncookie.diary.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("com.startingblue.fourtooncookie.diary")
public class DiaryExceptionControllerAdvice {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Diary not found: " + e.getMessage());
    }

    @ExceptionHandler(DiaryNoSuchElementException.class)
    public ResponseEntity<String> handleDiaryNoSuchElementException(DiaryNoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Diary not found: " + e.getMessage());
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleDiaryNoSuchElementException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Diary default image path not found:  " + e.getMessage());
    }
}
