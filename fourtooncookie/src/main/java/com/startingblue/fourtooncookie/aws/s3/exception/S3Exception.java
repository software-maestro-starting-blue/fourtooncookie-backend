package com.startingblue.fourtooncookie.aws.s3.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class S3Exception extends RuntimeException {

    public S3Exception(String message) {
        super(message);
    }

    public S3Exception(String message, Throwable cause) {
        super(message, cause);
    }
}