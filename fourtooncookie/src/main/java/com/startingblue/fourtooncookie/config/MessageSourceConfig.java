package com.startingblue.fourtooncookie.config;

import com.startingblue.fourtooncookie.messagesource.XmlMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Locale;

@Configuration
public class MessageSourceConfig implements WebMvcConfigurer {

    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    private static final List<Locale> SUPPORTED_LOCALES = List.of(Locale.KOREAN, DEFAULT_LOCALE);

    @Bean
    public MessageSource messageSource() {
        XmlMessageSource messageSource = new XmlMessageSource();

        messageSource.setDefaultLocale(DEFAULT_LOCALE);
        for (Locale supportedLocale : SUPPORTED_LOCALES) {
            messageSource.setMessages(supportedLocale);
        }

        return messageSource;
    }

}
