package com.startingblue.fourtooncookie.jwt;

import com.startingblue.fourtooncookie.config.authentication.AuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtExtractorTest {

    private JwtExtractor jwtExtractor;
    private Key secretKey;

    @BeforeEach
    void setUp() {
        jwtExtractor = new JwtExtractor();

        // SECRET_KEY 설정
        String secret = "my-secret-key-my-secret-key-my-secret-key-12345";  // 256-bit key
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        // ReflectionTestUtils를 사용하여 private 필드 주입
        ReflectionTestUtils.setField(jwtExtractor, "SECRET_KEY", secret);
        ReflectionTestUtils.setField(jwtExtractor, "ISSUER", "test-issuer");
    }

    @DisplayName("유효한 JWT 토큰을 파싱하여 Claims를 반환한다.")
    @Test
    void parseTokenTest() {
        // given
        String token = createJwtToken();

        // when
        Claims claims = jwtExtractor.parseToken(token);

        // then
        assertThat(claims.getIssuer()).isEqualTo("test-issuer");
        assertThat(claims.getSubject()).isEqualTo("test-user");
    }

    @DisplayName("잘못된 서명의 JWT 토큰을 파싱하면 AuthenticationException을 던진다.")
    @Test
    void parseTokenWithInvalidSignatureTest() {
        // given
        String invalidToken = createJwtTokenWithInvalidSignature();

        // when & then
        assertThatThrownBy(() -> jwtExtractor.parseToken(invalidToken))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid JWT signature");
    }

    @DisplayName("유효한 Bearer 토큰을 추출한다.")
    @Test
    void resolveTokenTest() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-jwt-token");

        // when
        String token = jwtExtractor.resolveToken(request);

        // then
        assertThat(token).isEqualTo("valid-jwt-token");
    }

    @DisplayName("Authorization 헤더가 없을 경우 AuthenticationException을 던진다.")
    @Test
    void resolveTokenWithoutAuthorizationHeaderTest() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when & then
        assertThatThrownBy(() -> jwtExtractor.resolveToken(request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Token does not exist.");
    }

    @DisplayName("Bearer 토큰 형식이 아닐 경우 AuthenticationException을 던진다.")
    @Test
    void resolveTokenWithInvalidBearerTest() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "InvalidTokenFormat");

        // when & then
        assertThatThrownBy(() -> jwtExtractor.resolveToken(request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Token does not exist.");
    }

    @DisplayName("만료된 JWT 토큰을 파싱할 때 AuthenticationException이 발생한다.")
    @Test
    void parseExpiredTokenTest() {
        // given
        String expiredToken = createExpiredJwtToken();

        // when & then
        assertThatThrownBy(() -> jwtExtractor.parseToken(expiredToken))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Expired JWT token");
    }

    @DisplayName("잘못된 형식의 JWT 토큰을 파싱할 때 AuthenticationException이 발생한다.")
    @Test
    void parseMalformedTokenTest() {
        // given
        String malformedToken = "this-is-not-a-valid-token";

        // when & then
        assertThatThrownBy(() -> jwtExtractor.parseToken(malformedToken))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Malformed JWT token");
    }

    @DisplayName("잘못된 JWT 구성 요소로 인해 IllegalArgumentException이 발생한다.")
    @Test
    void parseTokenWithInvalidArgumentsTest() {
        // given
        String invalidToken = "";  // 빈 문자열

        // when & then
        assertThatThrownBy(() -> jwtExtractor.parseToken(invalidToken))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("JWT token compact of handler are invalid");
    }

    private String createJwtToken() {
        return Jwts.builder()
                .setIssuer("test-issuer")
                .setSubject("test-user")
                .setExpiration(new Date(System.currentTimeMillis() + 60000)) // 1분 뒤 만료
                .signWith(secretKey)
                .compact();
    }

    private String createJwtTokenWithInvalidSignature() {
        Key anotherKey = Keys.hmacShaKeyFor("another-secret-key-another-secret-key-12345".getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setIssuer("test-issuer")
                .setSubject("test-user")
                .setExpiration(new Date(System.currentTimeMillis() + 60000)) // 1분 뒤 만료
                .signWith(anotherKey)  // 잘못된 키로 서명
                .compact();
    }

    private String createExpiredJwtToken() {
        return Jwts.builder()
                .setIssuer("test-issuer")
                .setSubject("test-user")
                .setExpiration(new Date(System.currentTimeMillis() - 60000)) // 이미 만료된 토큰
                .signWith(secretKey)
                .compact();
    }

}
