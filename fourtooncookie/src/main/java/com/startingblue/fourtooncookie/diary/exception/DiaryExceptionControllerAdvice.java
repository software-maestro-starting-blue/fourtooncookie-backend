package com.startingblue.fourtooncookie.diary.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("com.startingblue.fourtooncookie.diary")
@Slf4j
public class DiaryExceptionControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleConstraintViolationException(ConstraintViolationException e) {
        log.error(e.getMessage());
        return "Diary constraint violation";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFoundException(EntityNotFoundException e) {
        log.error(e.getMessage());
        return "Diary not found";
    }

    @ExceptionHandler(DiaryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleDiaryNotFoundException(DiaryNotFoundException e) {
        log.error(e.getMessage());
        return "Diary not found";
    }

    @ExceptionHandler(DiaryDuplicateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDiaryDuplicateException(DiaryDuplicateException e) {
        log.error(e.getMessage());
        return "Diary duplicate";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception e) {
        log.error(e.getMessage());
        return "Server Error";
    }

}
