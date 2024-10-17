package com.startingblue.fourtooncookie.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;
import java.util.Map;

@RequiredArgsConstructor
public class CustomLocaleChangeInterceptor extends LocaleChangeInterceptor {

    private final LocaleResolver localeResolver;
    private static final Locale DEFAULT_LANGUAGE = Locale.ENGLISH;
    private static final Map<String, String> SUPPORTED_LANGUAGES = Map.of(
            "ko", "ko",
            "ko-KR", "ko"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String language = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);
        Locale locale = DEFAULT_LANGUAGE;

        if (StringUtils.hasText(language)) {
            String selectedLanguage = language.split(",")[0];
            if (SUPPORTED_LANGUAGES.containsKey(selectedLanguage)) {
                locale = Locale.forLanguageTag(SUPPORTED_LANGUAGES.get(selectedLanguage));
            }
        }

        localeResolver.setLocale(request, response, locale);
        return true;
    }
}
