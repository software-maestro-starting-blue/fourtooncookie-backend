package com.startingblue.fourtooncookie.config.authentication;

import com.startingblue.fourtooncookie.jwt.JwtExtractor;
import com.startingblue.fourtooncookie.member.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
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
    private final AuthenticationExceptionHandler exceptionHandler;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException {
        try {
            String token = jwtExtractor.resolveToken(request);
            Claims claims = parseToken(token);
            UUID memberId = UUID.fromString(claims.getSubject());

            if (memberService.verifyMemberExists(memberId)) {
                request.setAttribute("memberId", memberId);
                chain.doFilter(request, response);
            } else {
                   throw new EntityNotFoundException();
            }
        } catch (AuthenticationException e) {
            exceptionHandler.handleAuthenticationException(e, response);
        } catch (Exception e) {
            exceptionHandler.handleGeneralException(e, response);
        }
    }

    private Claims parseToken(String token) {
        try {
            return jwtExtractor.parseToken(token);
        } catch (ExpiredJwtException e) {
            throw new AuthenticationException("Expired JWT token", HttpServletResponse.SC_UNAUTHORIZED, e);
        } catch (UnsupportedJwtException e) {
            throw new AuthenticationException("Unsupported JWT token", HttpServletResponse.SC_UNAUTHORIZED, e);
        } catch (MalformedJwtException e) {
            throw new AuthenticationException("Malformed JWT token", HttpServletResponse.SC_UNAUTHORIZED, e);
        } catch (SecurityException e) {
            throw new AuthenticationException("Invalid JWT signature", HttpServletResponse.SC_UNAUTHORIZED, e);
        } catch (IllegalArgumentException e) {
            throw new AuthenticationException("JWT token compact of handler are invalid", HttpServletResponse.SC_UNAUTHORIZED, e);
        }
    }

}
