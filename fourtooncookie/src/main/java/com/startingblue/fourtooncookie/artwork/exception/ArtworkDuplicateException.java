package com.startingblue.fourtooncookie.artwork.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ArtworkDuplicateException extends RuntimeException{
    public ArtworkDuplicateException(String message) {
        super(message);
    }
}
