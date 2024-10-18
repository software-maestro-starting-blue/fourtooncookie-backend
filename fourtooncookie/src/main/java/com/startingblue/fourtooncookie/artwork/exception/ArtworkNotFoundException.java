package com.startingblue.fourtooncookie.artwork.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ArtworkNotFoundException extends RuntimeException {
    public ArtworkNotFoundException(String message) {
        super(message);
    }
}
