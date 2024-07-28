package com.startingblue.fourtooncookie.config.authentication;

import com.startingblue.fourtooncookie.jwt.JwtExtractor;
import com.startingblue.fourtooncookie.member.domain.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
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
    private final MemberRepository memberRepository;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException {
        try {
            String token = jwtExtractor.resolveToken(request);
            Claims claims = parseToken(token);
            UUID memberId = UUID.fromString(claims.getSubject());
            verifyMemberExists(memberId);

            request.setAttribute("memberId", memberId);
            chain.doFilter(request, response);
        } catch (AuthenticationException e) {
            handleAuthenticationException(e, response);
        } catch (Exception e) {
            handleGeneralException(e, response);
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

    private void verifyMemberExists(UUID memberId) {
        boolean existsById = memberRepository.existsById(memberId);
        if (!existsById) {
            log.error("Member ID not found: {}", memberId); // 멤버 ID가 없음을 경고 로그로 기록
            throw new AuthenticationException("Member ID not found", HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private void handleAuthenticationException(AuthenticationException e, HttpServletResponse response) throws IOException {
        log.error(e.getMessage(), e); // 인증 예외를 에러 로그로 기록
        response.setStatus(e.getStatusCode());
        response.getWriter().write(e.getMessage());
    }

    private void handleGeneralException(Exception e, HttpServletResponse response) throws IOException {
        log.error("Authentication error", e); // 일반 예외를 에러 로그로 기록
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write("Internal server error");
    }
}
