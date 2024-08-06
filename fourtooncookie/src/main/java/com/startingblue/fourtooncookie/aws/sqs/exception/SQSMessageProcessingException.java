package com.startingblue.fourtooncookie.aws.sqs.exception;

public class SQSMessageProcessingException extends RuntimeException {
    public SQSMessageProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}