package com.startingblue.fourtooncookie.diary.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("com.startingblue.fourtooncookie.diary")
public class DiaryExceptionControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleConstraintViolationException(ConstraintViolationException e) {
        return "Diary constraint violation " + e.getMessage();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFoundException(EntityNotFoundException e) {
        return "Diary not found: " + e.getMessage();
    }

    @ExceptionHandler(DiaryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleDiaryNotFoundException(DiaryNotFoundException e) {
        return "Diary not found: " + e.getMessage();
    }

    @ExceptionHandler(DiaryDuplicateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDiaryDuplicateException(DiaryDuplicateException e) {
        return "Diary duplicate: " + e.getMessage();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception e) {
        return e.getMessage();
    }

}
