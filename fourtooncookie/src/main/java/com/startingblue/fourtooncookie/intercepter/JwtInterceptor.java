package com.startingblue.fourtooncookie.intercepter;

import com.startingblue.fourtooncookie.jwt.JwtExtractor;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = resolveToken(request);
        log.info("token: {}", token);
        if (token != null && !token.isEmpty()) {
            try {
                Claims claims = JwtExtractor.parseToken(token);
                request.setAttribute("memberId", claims.get("memberId", Long.class));
                log.info("memberId: {}", claims.get("memberId"));
                return true;
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                log.info("invalid or expired token");
                return false;
            }
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing token");
            log.info("missing token");
            return false;
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
