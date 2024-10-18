package com.startingblue.fourtooncookie.aws.s3.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class S3PreSignUrlException extends S3Exception {
    public S3PreSignUrlException(String message, Throwable cause) {
        super(message, cause);
    }
}
