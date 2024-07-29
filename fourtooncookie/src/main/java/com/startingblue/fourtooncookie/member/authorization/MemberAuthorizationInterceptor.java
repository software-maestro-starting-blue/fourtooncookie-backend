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

@RequiredArgsConstructor
@Component
@Slf4j
public final class MemberAuthorizationInterceptor implements HandlerInterceptor {

    private static final String PATH_VARIABLE_KEY = "memberId";

    private final JwtExtractor jwtExtractor;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {
        final String token = jwtExtractor.resolveToken(request);
        if (!token.isEmpty()) {
            final Claims claims = jwtExtractor.parseToken(token);
            final UUID memberId = UUID.fromString(claims.getSubject());

            final Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            if (Objects.isNull(pathVariables.get(PATH_VARIABLE_KEY))) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return false;
            }
            final UUID pathVariableMemberID = UUID.fromString(pathVariables.get(PATH_VARIABLE_KEY));

            if (Objects.equals(memberId, pathVariableMemberID)) {
                return true;
            }
        }
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return false;
    }
}
