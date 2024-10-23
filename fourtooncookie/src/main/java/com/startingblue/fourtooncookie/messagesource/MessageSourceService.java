package com.startingblue.fourtooncookie.messagesource;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MessageSourceService {

    private final MessageSource messageSource;

    public String getMessage(String code, Locale locale) {
        return getMessage(code, null, null, locale);
    }

    public String getMessage(String code, Object[] args, Locale locale) {
        return getMessage(code, args, null, locale);
    }

    public String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale) {
        return messageSource.getMessage(code, args, defaultMessage, locale);
    }
}
