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

        if (isBypassAvailable(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        UUID memberId = extractMemberId(request);

        if (memberId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token.");
            return;
        }

        if (isSignupRequest(requestURI, request.getMethod())) {
            handleSignup(request, response, chain, memberId);
        } else {
            handleLogin(request, response, chain, memberId);
        }
    }

    private boolean isBypassAvailable(String requestURI) {
        return requestURI.startsWith("/h2-console") || requestURI.startsWith("/health");
    }

    private UUID extractMemberId(HttpServletRequest request) {
        String token = jwtExtractor.resolveToken(request);
        if (token == null) {
            log.warn("Token not found in request");
            return null;
        }

        Claims claims = jwtExtractor.parseToken(token);
        return UUID.fromString(claims.getSubject());
    }

    private boolean isSignupRequest(String requestURI, String method) {
        return requestURI.startsWith("/member") && "POST".equalsIgnoreCase(method);
    }

    private void handleSignup(HttpServletRequest request, HttpServletResponse response, FilterChain chain, UUID memberId)
            throws IOException, ServletException {
        if (memberService.verifyMemberExists(memberId)) {
            log.error("[{}] Signup attempt failed: Account already exists", memberId);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Account already exists.");
            return;
        }

        log.info("[{}] Signup request received", memberId);
        request.setAttribute("memberId", memberId);
        chain.doFilter(request, response);
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response, FilterChain chain, UUID memberId)
            throws IOException, ServletException {
        log.info("[{}] Login attempt", memberId);
        if (memberService.verifyMemberExists(memberId)) {
            log.info("[{}] Login success", memberId);
            request.setAttribute("memberId", memberId);
            chain.doFilter(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    String.format("Member with id %s not found", memberId));
        }
    }
}
