package com.startingblue.fourtooncookie.character.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("com.startingblue.fourtooncookie.character")
@Slf4j
public class CharacterExceptionControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleConstraintViolationException(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        return "Character Constraint Violation";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFoundExceptionException(EntityNotFoundException e) {
        log.error(e.getMessage(), e);
        return "Character Not Found";
    }

    @ExceptionHandler(CharacterNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleCharacterNoSuchElementException(CharacterNotFoundException e) {
        log.error(e.getMessage(), e);
        return "Character Not Found";
    }

    @ExceptionHandler(CharacterVisionTypeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundCharacterVisionTypeException(CharacterVisionTypeNotFoundException e) {
        log.error(e.getMessage(), e);
        return "Character Vision Type not found";
    }

    @ExceptionHandler(CharacterDuplicateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleCharacterDuplicateException(CharacterDuplicateException e) {
        log.error(e.getMessage(), e);
        return "Bad Request";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException e) {
        log.error(e.getMessage(), e);
        return "Bad Request";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception e) {
        log.error(e.getMessage(), e);
        return "Character Server Error";
    }
}
