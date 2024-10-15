package com.startingblue.fourtooncookie.web;

import com.startingblue.fourtooncookie.web.exception.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Objects;
import java.util.UUID;

@Component
public class ArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return UUID.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        HttpServletRequest request = getRequest(webRequest);
        String memberId = getMemberId(request);
        return parseMemberId(memberId);
    }

    HttpServletRequest getRequest(NativeWebRequest webRequest) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        Objects.requireNonNull(request, "HttpServletRequest must not be null");

        return request;
    }

    String getMemberId(HttpServletRequest request) {
        String memberId = String.valueOf(request.getAttribute("memberId"));

        if (!StringUtils.hasText(memberId)) {
            throw new AuthenticationException("Member ID is missing");
        }

        return memberId;
    }

    UUID parseMemberId(String memberId) {
        try {
            return UUID.fromString(memberId);
        } catch (IllegalArgumentException e) {
            throw new AuthenticationException("Invalid member ID format", e);
        }
    }
}
