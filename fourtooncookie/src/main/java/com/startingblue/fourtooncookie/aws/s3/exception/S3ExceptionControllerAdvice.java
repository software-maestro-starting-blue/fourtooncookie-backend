package com.startingblue.fourtooncookie.aws.s3.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class S3ExceptionControllerAdvice {

    @ExceptionHandler(S3Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleS3Exception(S3Exception e) {
        log.error("S3 Server Error: {}", e.getMessage(), e);
        return "Server Error";
    }

    @ExceptionHandler(S3UploadException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleS3UploadException(S3UploadException e) {
        log.error("S3 Upload Error: {}", e.getMessage(), e);
        return "Upload Error";
    }

    @ExceptionHandler(S3PreSignUrlException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleS3PresignUrlException(S3PreSignUrlException e) {
        log.error("S3 Presign URL Error: {}", e.getMessage(), e);
        return "Presign URL Error";
    }

    @ExceptionHandler(S3ImageNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleS3ImageNotFoundException(S3ImageNotFoundException e) {
        log.error("S3 Image Not Found: {}", e.getMessage(), e);
        return "Image Not Found";
    }

    @ExceptionHandler(S3ImageExistenceCheckException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleS3ImageExistenceCheckException(S3ImageExistenceCheckException e) {
        log.error("S3 Image Existence Check Error: {}", e.getMessage(), e);
        return "Image Existence Check Error";
    }
}
