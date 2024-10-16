package com.startingblue.fourtooncookie.global.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.ENGLISH); // 기본 로케일 설정
        return localeResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        return new LocaleChangeInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
                String langParam = request.getParameter("lang");
                if (!StringUtils.hasText(langParam)) {
                    // Accept-Language 헤더 우선 처리
                    langParam = request.getHeader("Accept-Language");
                    if (langParam == null) {
                        langParam = "en"; // 기본 값 영어로
                    } else {
                        // Accept-Language에서 첫 번째 언어를 선택 (Accept-Language: ko, en;q=0.8, ja;q=0.6) 헤더 정보가 형태임
                        langParam = langParam.split(",")[0].split("-")[0];
                    }
                }

                LocaleResolver localeResolver = localeResolver();
                if ("ko".equals(langParam)) {
                    localeResolver.setLocale(request, response, Locale.KOREA);
                } else if ("en".equals(langParam)) {
                    localeResolver.setLocale(request, response, Locale.ENGLISH);
                } else {
                    localeResolver.setLocale(request, response, Locale.ENGLISH); // 기본값 설정
                }

                return super.preHandle(request, response, handler);
            }
        };
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}