package com.startingblue.fourtooncookie.web;

import com.startingblue.fourtooncookie.web.exception.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

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
        String memberIdString = getMemberIdString(request);
        return parseMemberId(memberIdString);
    }

    private HttpServletRequest getRequest(NativeWebRequest webRequest) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            throw new IllegalStateException("HttpServletRequest must not be null");
        }
        return request;
    }

    private String getMemberIdString(HttpServletRequest request) {
        String memberIdString = String.valueOf(request.getAttribute("memberId"));
        if (memberIdString == null || memberIdString.isEmpty()) {
            throw new AuthenticationException("Member ID is missing");
        }
        return memberIdString;
    }

    private UUID parseMemberId(String memberIdString) {
        try {
            return UUID.fromString(memberIdString);
        } catch (IllegalArgumentException e) {
            throw new AuthenticationException("Invalid member ID format", e);
        }
    }
}
