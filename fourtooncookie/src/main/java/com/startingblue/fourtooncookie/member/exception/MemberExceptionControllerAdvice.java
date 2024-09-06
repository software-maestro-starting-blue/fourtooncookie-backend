package com.startingblue.fourtooncookie.member.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("com.startingblue.fourtooncookie.member")
@Slf4j
public class MemberExceptionControllerAdvice {

    @ExceptionHandler({ EntityNotFoundException.class, MemberNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFoundExceptionException(EntityNotFoundException e) {
        log.error(e.getMessage(), e);
        return "Member Not Found";
    }

    @ExceptionHandler(MemberDuplicateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleMemberDuplicateException(MemberDuplicateException e) {
        log.error(e.getMessage(), e);
        return "Member Exists";
    }
}
