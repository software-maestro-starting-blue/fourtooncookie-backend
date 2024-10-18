package com.startingblue.fourtooncookie.aws.s3.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class S3UploadException extends S3Exception {
    public S3UploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
