package com.startingblue.fourtooncookie.config.authentication;

import com.startingblue.fourtooncookie.jwt.JwtExtractor;
import com.startingblue.fourtooncookie.member.domain.MemberRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;
import java.util.UUID;


@RequiredArgsConstructor
@Component
@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {

    private static final String PREFIX_BEARER = "Bearer ";

    private final JwtExtractor jwtExtractor;
    private final MemberRepository memberRepository;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {
        final String token = resolveToken(request);
        if (!token.isEmpty()) {
            final Claims claims = jwtExtractor.parseToken(token);
            final UUID memberId = UUID.fromString(claims.getSubject());

            boolean existsById = memberRepository.existsById(memberId);
            if (existsById) {
                request.setAttribute("memberId", memberId);
                return true;
            }
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }

    private String resolveToken(final HttpServletRequest request) {
        final String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Objects.nonNull(bearerToken) && bearerToken.startsWith(PREFIX_BEARER)) {
            return bearerToken.substring(PREFIX_BEARER.length());
        }
        throw new IllegalArgumentException("토큰 에러");
    }
}
