package com.startingblue.fourtooncookie.artwork.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("com.startingblue.fourtooncookie.artwork")
@Slf4j
public class ArtworkExceptionControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleConstraintViolationException(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        return "Artwork Constraint Violation";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFoundException(EntityNotFoundException e) {
        log.error(e.getMessage(), e);
        return "Artwork not found";
    }

    @ExceptionHandler(ArtworkNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleArtworkNotFoundException(ArtworkNotFoundException e) {
        log.error(e.getMessage(), e);
        return "Artwork not found";
    }

    @ExceptionHandler(ArtworkDuplicateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleArtworkDuplicateException(ArtworkDuplicateException e) {
        log.error(e.getMessage(), e);
        return "Artwork duplicate";
    }

}
