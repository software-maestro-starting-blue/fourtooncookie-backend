package com.startingblue.fourtooncookie.translation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TranslationObjectClassIdNotFoundException extends NoSuchElementException {
}
