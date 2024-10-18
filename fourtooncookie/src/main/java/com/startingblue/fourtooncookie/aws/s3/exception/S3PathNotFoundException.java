package com.startingblue.fourtooncookie.aws.s3.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class S3PathNotFoundException extends S3Exception {
    public S3PathNotFoundException(String message) {
        super(message);
    }
}