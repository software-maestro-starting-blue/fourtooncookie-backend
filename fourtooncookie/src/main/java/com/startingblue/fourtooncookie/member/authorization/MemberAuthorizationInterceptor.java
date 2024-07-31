package com.startingblue.fourtooncookie.member.authorization;

import com.startingblue.fourtooncookie.jwt.JwtExtractor;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

@RequiredArgsConstructor
@Component
@Slf4j
public final class MemberAuthorizationInterceptor implements HandlerInterceptor {

    private static final String PATH_VARIABLE_KEY = "memberId";

    private final JwtExtractor jwtExtractor;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {
        final String token = jwtExtractor.resolveToken(request);
        if (token.isEmpty()) {
            response.setStatus(SC_FORBIDDEN);
            return false;
        }

        final Claims claims = jwtExtractor.parseToken(token);
        final UUID memberId = UUID.fromString(claims.getSubject());

        final Map<String, String> pathVariables = getPathVariables(request);
        if (!pathVariables.containsKey(PATH_VARIABLE_KEY)) {
            response.setStatus(SC_NOT_FOUND);
            return false;
        }

        final UUID pathVariableMemberID = UUID.fromString(pathVariables.get(PATH_VARIABLE_KEY));
        if (isAuthorized(memberId, pathVariableMemberID)) {
            return true;
        }

        response.setStatus(SC_FORBIDDEN);
        return false;
    }

    private Map<String, String> getPathVariables(final HttpServletRequest request) {
        return (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    }

    private boolean isAuthorized(final UUID memberId, final UUID pathVariableMemberID) {
        return Objects.equals(memberId, pathVariableMemberID);
    }
}
