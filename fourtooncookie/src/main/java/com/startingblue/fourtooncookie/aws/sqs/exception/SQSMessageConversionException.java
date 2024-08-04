package com.startingblue.fourtooncookie.aws.sqs.exception;

public class SQSMessageConversionException extends RuntimeException {
    public SQSMessageConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
