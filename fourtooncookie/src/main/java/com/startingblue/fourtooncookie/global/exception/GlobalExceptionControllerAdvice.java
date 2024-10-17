package com.startingblue.fourtooncookie.global.exception;

import com.startingblue.fourtooncookie.artwork.exception.ArtworkNotFoundException;
import com.startingblue.fourtooncookie.character.exception.CharacterNotFoundException;
import com.startingblue.fourtooncookie.web.exception.AuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("Validation error: ", e);
        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return errors;
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleAuthenticationException(AuthenticationException e) {
        log.error("Authentication Filter error: ", e);
        return "Authentication Filter error";
    }

    @ExceptionHandler(CharacterNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleCharacterNotFoundException(CharacterNotFoundException e) {
        log.error("Filter Character not found: ", e);
        return "Character not found";
    }

    @ExceptionHandler(ArtworkNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleArtworkNotFoundException(ArtworkNotFoundException e) {
        log.error("Filter Artwork not found: ", e);
        return "Artwork not found";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException e) {
        log.error(e.getMessage(), e);
        return "Bad Request";
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoSuchElementException(IllegalArgumentException e) {
        log.error(e.getMessage(), e);
        return "Not Found";
    }
}
