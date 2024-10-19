package com.startingblue.fourtooncookie.config;

import com.startingblue.fourtooncookie.locale.XmlMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Locale;

@Configuration
public class MessageSourceConfig implements WebMvcConfigurer {

    private final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    private final List<Locale> supportedLocale = List.of(Locale.KOREAN);

    @Bean
    public MessageSource messageSource() {
        XmlMessageSource xmlMessageSource = new XmlMessageSource();

        xmlMessageSource.setDefaultLocale(DEFAULT_LOCALE);
        for (Locale locale : supportedLocale) {
            xmlMessageSource.setMessages(locale);
        }

        return xmlMessageSource;
    }
}
