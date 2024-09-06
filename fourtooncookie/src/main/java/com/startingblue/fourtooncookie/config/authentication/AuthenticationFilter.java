package com.startingblue.fourtooncookie.config.authentication;

import com.startingblue.fourtooncookie.jwt.JwtExtractor;
import com.startingblue.fourtooncookie.member.service.MemberService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class AuthenticationFilter extends HttpFilter {

    private final JwtExtractor jwtExtractor;
    private final MemberService memberService;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String requestURI = request.getRequestURI();
        try {
            if (requestURI.startsWith("/h2-console") || requestURI.startsWith("/health")) {
                chain.doFilter(request, response);
                return;
            }

            String token = jwtExtractor.resolveToken(request);
            Claims claims = jwtExtractor.parseToken(token);
            UUID memberId = UUID.fromString(claims.getSubject());
            log.info("login attempt memberId: {}", memberId);

            if (memberService.verifyMemberExists(memberId)) {
                log.info("login success memberId: {}", memberId);
                request.setAttribute("memberId", memberId);
                chain.doFilter(request, response);
            } else {
                if (isSignupRequest(requestURI, request.getMethod())) {
                    request.setAttribute("memberId", memberId);
                    chain.doFilter(request, response);
                    return;
                }
                log.error("Member with id {} not found", memberId);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, String.format("Member with id %s not found", memberId));
            }
        } catch (AuthenticationException e) {
            log.error("Authentication failed: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed: " + e.getMessage());
        }
    }

    private boolean isSignupRequest(String requestURI, String method) {
        return requestURI.equalsIgnoreCase("/member") && "POST".equalsIgnoreCase(method);
    }
}
