package com.startingblue.fourtooncookie.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;


@Configuration
@RequiredArgsConstructor
public class LocaleConfig implements WebMvcConfigurer {

    private static final List<Locale> SUPPORTED_LOCALES = List.of(Locale.KOREAN);

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.ENGLISH);
        localeResolver.setSupportedLocales(SUPPORTED_LOCALES);
        return localeResolver;
    }

}
