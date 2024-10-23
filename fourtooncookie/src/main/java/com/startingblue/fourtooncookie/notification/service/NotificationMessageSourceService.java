package com.startingblue.fourtooncookie.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationMessageSourceService {

    private final MessageSource messageSource;

    public String getMessage(String code) {
        return messageSource.getMessage(code, null, null, LocaleContextHolder.getLocale());
    }
}
