package com.startingblue.fourtooncookie.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.security.Key;

public class JwtExtractor {
    private static final Key SECRET_KEY = JwtProvider.getSecretKey();

    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
