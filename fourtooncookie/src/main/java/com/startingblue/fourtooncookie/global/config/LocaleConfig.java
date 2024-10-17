package com.startingblue.fourtooncookie.global.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.*;

@Configuration
public class LocaleConfig implements WebMvcConfigurer {

    private static final Locale DEFAULT_LANGUAGE = Locale.ENGLISH;
    private static final Map<String, String> SUPPORTED_LANGUAGES = new HashMap<>();

    static {
        SUPPORTED_LANGUAGES.put("ko", "ko");
        SUPPORTED_LANGUAGES.put("ko-KR", "ko");
    }

    @Bean
    public LocaleResolver localeResolver() {
        return new SessionLocaleResolver();
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor(LocaleResolver localeResolver) {
        return new LocaleChangeInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
                String language = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);
                Locale locale = DEFAULT_LANGUAGE;

                if (StringUtils.hasText(language)) {
                    language = language.split(",")[0];
                    if (SUPPORTED_LANGUAGES.containsKey(language)) {
                        locale = Locale.forLanguageTag(SUPPORTED_LANGUAGES.get(language));
                    }
                }

                localeResolver.setLocale(request, response, locale);

                return super.preHandle(request, response, handler);
            }
        };
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor(localeResolver()));
    }
}
