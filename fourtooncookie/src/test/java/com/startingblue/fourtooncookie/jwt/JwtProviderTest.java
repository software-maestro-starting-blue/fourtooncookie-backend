package com.startingblue.fourtooncookie.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class JwtProviderTest {
    @Test
    public void testGenerateToken() {
        Long memberId = 1L;
        String token = JwtProvider.generateToken(memberId);

        assertNotNull(token, "Token should not be null");
        System.out.println("Generated Token: " + token);

        // Parse the token to validate its contents
        Key key = JwtProvider.getSecretKey();
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("member", claims.getSubject(), "Subject should be 'member'");
        assertEquals(memberId, claims.get("memberId", Long.class), "memberId should match");
        assertTrue(claims.getExpiration().after(new Date()), "Token should not be expired");
    }
}
