package com.startingblue.fourtooncookie.aws.s3.exception;

public class S3ImageExistenceCheckException extends S3Exception {

    public S3ImageExistenceCheckException(String message, Throwable cause) {
        super(message, cause);
    }
}