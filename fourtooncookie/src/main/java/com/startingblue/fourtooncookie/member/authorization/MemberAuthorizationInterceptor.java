package com.startingblue.fourtooncookie.member.authorization;

import com.startingblue.fourtooncookie.jwt.JwtExtractor;
import com.startingblue.fourtooncookie.member.authorization.exception.InvalidPathVariableException;
import com.startingblue.fourtooncookie.member.authorization.exception.TokenNotFoundException;
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
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            final String token = extractToken(request);
            final Claims claims = jwtExtractor.parseToken(token);
            final UUID memberId = extractMemberId(claims);
            final UUID pathVariableMemberID = extractPathVariableMemberId(request);

            if (!isAuthorized(memberId, pathVariableMemberID)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
            return true;
        } catch (TokenNotFoundException | InvalidPathVariableException e) {
            log.error("Authorization error: ", e);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
    }

    private String extractToken(HttpServletRequest request) throws TokenNotFoundException {
        String token = jwtExtractor.resolveToken(request);
        if (token == null || token.isEmpty()) {
            throw new TokenNotFoundException("No token provided.");
        }
        return token;
    }

    private UUID extractMemberId(Claims claims) throws InvalidPathVariableException {
        try {
            return UUID.fromString(claims.getSubject());
        } catch (IllegalArgumentException e) {
            log.error("Invalid member ID in JWT", e);
            throw new InvalidPathVariableException("Invalid member ID in JWT");
        }
    }

    private UUID extractPathVariableMemberId(HttpServletRequest request) throws InvalidPathVariableException {
        Map<String, String> pathVariables = getPathVariables(request);
        String memberIdStr = pathVariables.get(PATH_VARIABLE_KEY);
        if (memberIdStr == null) {
            throw new InvalidPathVariableException("Member ID path variable not found");
        }
        try {
            return UUID.fromString(memberIdStr);
        } catch (IllegalArgumentException e) {
            log.error("Error extracting member ID from path variables", e);
            throw new InvalidPathVariableException("Member ID path variable is not valid UUID");
        }
    }

    private Map<String, String> getPathVariables(HttpServletRequest request) {
        return (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    }

    private boolean isAuthorized(UUID memberId, UUID pathVariableMemberId) {
        return Objects.equals(memberId, pathVariableMemberId);
    }
}
