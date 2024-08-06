package com.startingblue.fourtooncookie.aws.sqs.exception;

public class SQSMessageDeletionException extends RuntimeException {
    public SQSMessageDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}