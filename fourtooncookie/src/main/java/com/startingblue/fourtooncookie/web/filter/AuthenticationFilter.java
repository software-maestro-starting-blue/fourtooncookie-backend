package com.startingblue.fourtooncookie.web.filter;

import com.startingblue.fourtooncookie.web.exception.AuthenticationException;
import com.startingblue.fourtooncookie.member.MemberService;
import com.startingblue.fourtooncookie.web.filter.jwt.JwtExtractor;
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
        log.info("doFilter() called with requestURI: {}, method: {}", request.getRequestURI(), request.getMethod());

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        if (shouldBypassAuthentication(requestURI, method)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            UUID memberId = extractUUIDFromToken(request);
            log.info("Login attempt for memberId: {}", memberId);

            if (isSignupRequest(requestURI, method)) {
                log.info("Processing signup request for URI: {}", requestURI);
                setMemberIdAttribute(request, memberId);
                chain.doFilter(request, response);
                return;
            }

            handleRequestByUUID(request, response, chain, memberId);

        } catch (AuthenticationException e) {
            log.error("Authentication failed: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed: " + e.getMessage());
        }
    }

    private boolean shouldBypassAuthentication(String requestURI, String method) {
        return requestURI.startsWith("/h2-console") || requestURI.startsWith("/health") ||
                (method.equalsIgnoreCase("GET") && (requestURI.startsWith("/character") || requestURI.startsWith("/artwork")));
    }

    private UUID extractUUIDFromToken(HttpServletRequest request) {
        String token = jwtExtractor.resolveToken(request);
        Claims claims = jwtExtractor.parseToken(token);
        return UUID.fromString(claims.getSubject());
    }

    private void setMemberIdAttribute(HttpServletRequest request, UUID memberId) {
        request.setAttribute("memberId", memberId);
    }

    private void handleRequestByUUID(HttpServletRequest request, HttpServletResponse response, FilterChain chain, UUID memberId)
            throws IOException, ServletException {
        if (isExistsMember(memberId)) {
            log.info("Login success for memberId: {}", memberId);
            setMemberIdAttribute(request, memberId);
            chain.doFilter(request, response);
        } else {
            log.error("Member with id {} not found", memberId);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, String.format("Member with id %s not found", memberId));
        }
    }

    private boolean isSignupRequest(String requestURI, String method) {
        return "/member".equalsIgnoreCase(requestURI) && "POST".equalsIgnoreCase(method);
    }

    private boolean isExistsMember(UUID memberId) {
        return memberService.verifyMemberExists(memberId);
    }
}
