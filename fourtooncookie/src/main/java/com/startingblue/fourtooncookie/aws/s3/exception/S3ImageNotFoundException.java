package com.startingblue.fourtooncookie.aws.s3.exception;

public class S3ImageNotFoundException extends S3Exception {

    public S3ImageNotFoundException(String message) {
        super(message);
    }
}