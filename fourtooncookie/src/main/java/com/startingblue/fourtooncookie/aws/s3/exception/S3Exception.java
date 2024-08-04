package com.startingblue.fourtooncookie.aws.s3.exception;

public class S3Exception extends RuntimeException {
    public S3Exception(String message) {
        super(message);
    }

    public S3Exception(String message, Throwable cause) {
        super(message, cause);
    }
}