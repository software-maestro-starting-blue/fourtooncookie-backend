package com.startingblue.fourtooncookie.artwork.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("com.startingblue.fourtooncookie.artwork")
public class ArtworkExceptionControllerAdvice {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFoundException(EntityNotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler(ArtworkNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleArtworkNotFoundException(ArtworkNotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler(ArtworkDuplicateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleArtworkDuplicateException(ArtworkDuplicateException e) {
        return e.getMessage();
    }

}
