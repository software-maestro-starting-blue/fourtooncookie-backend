package com.startingblue.fourtooncookie.member.exception;

public class MemberDuplicateException extends RuntimeException{
    public MemberDuplicateException(String message) {
        super(message);
    }
}
