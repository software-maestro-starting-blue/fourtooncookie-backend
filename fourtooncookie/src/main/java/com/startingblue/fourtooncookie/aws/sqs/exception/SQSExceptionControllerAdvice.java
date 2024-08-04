package com.startingblue.fourtooncookie.aws.sqs.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class SQSExceptionControllerAdvice {

    @ExceptionHandler(SQSMessageProcessingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleSQSMessageProcessingException(SQSMessageProcessingException e) {
        log.error(e.getMessage(), e);
        return "SQS message processing failed";
    }

    @ExceptionHandler(SQSMessageConversionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleSQSMessageConversionException(SQSMessageConversionException e) {
        log.error(e.getMessage(), e);
        return "SQS message conversion failed";
    }

    @ExceptionHandler(SQSMessageDeletionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleSQSMessageDeletionException(SQSMessageDeletionException e) {
        log.error(e.getMessage(), e);
        return "SQS message deletion failed";
    }
}
