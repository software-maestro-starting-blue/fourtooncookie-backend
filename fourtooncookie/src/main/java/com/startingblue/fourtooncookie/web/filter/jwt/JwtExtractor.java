package com.startingblue.fourtooncookie.web.filter.jwt;

import com.startingblue.fourtooncookie.web.exception.AuthenticationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Objects;

@Component
public class JwtExtractor {

    private static final String PREFIX_BEARER = "Bearer ";

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.issuer-uri}")
    private String ISSUER;

    public Claims parseToken(final String token) {
        final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .requireIssuer(ISSUER)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
            throw new AuthenticationException("Invalid JWT signature", e);
        } catch (ExpiredJwtException e) {
            throw new AuthenticationException("Expired JWT token", e);
        } catch (UnsupportedJwtException e) {
            throw new AuthenticationException("Unsupported JWT token", e);
        } catch (MalformedJwtException e) {
            throw new AuthenticationException("Malformed JWT token", e);
        } catch (IllegalArgumentException e) {
            throw new AuthenticationException("JWT token compact of handler are invalid", e);
        }
    }

    public String resolveToken(final HttpServletRequest request) {
        final String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Objects.nonNull(bearerToken) && bearerToken.startsWith(PREFIX_BEARER)) {
            return bearerToken.substring(PREFIX_BEARER.length());
        }
        throw new AuthenticationException("Token does not exist.");
    }
}
