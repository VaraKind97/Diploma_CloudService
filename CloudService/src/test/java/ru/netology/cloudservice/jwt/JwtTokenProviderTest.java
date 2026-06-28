package ru.netology.cloudservice.jwt;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(
                "mySecretKeyForTests123456789012345"
        );
    }

    @Test
    void createToken() {

        String token = jwtTokenProvider.createToken(
                "user",
                Map.of("role", "USER")
        );

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void validateTokenSuccess() {

        String token = jwtTokenProvider.createToken(
                "user",
                Map.of()
        );

        assertTrue(
                jwtTokenProvider.validateToken(token)
        );
    }

    @Test
    void validateTokenInvalid() {

        assertFalse(
                jwtTokenProvider.validateToken(
                        "invalid.jwt.token"
                )
        );
    }

    @Test
    void getClaimsFromToken() {

        String token = jwtTokenProvider.createToken(
                "user",
                Map.of(
                        "role", "USER",
                        "id", 1
                )
        );

        Claims claims =
                jwtTokenProvider.getClaimsFromToken(token);

        assertEquals(
                "user",
                claims.getSubject()
        );

        assertEquals(
                "USER",
                claims.get("role")
        );

        assertEquals(
                1,
                ((Number) claims.get("id")).intValue()
        );
    }

    @Test
    void subjectShouldMatchUsername() {

        String token = jwtTokenProvider.createToken(
                "admin",
                Map.of()
        );

        Claims claims =
                jwtTokenProvider.getClaimsFromToken(token);

        assertEquals(
                "admin",
                claims.getSubject()
        );
    }

    @Test
    void customClaimsShouldBeStored() {

        String token = jwtTokenProvider.createToken(
                "user",
                Map.of(
                        "email",
                        "user@test.com"
                )
        );

        Claims claims =
                jwtTokenProvider.getClaimsFromToken(token);

        assertEquals(
                "user@test.com",
                claims.get("email")
        );
    }

    @Test
    void validateTokenAfterClaimsExtraction() {

        String token = jwtTokenProvider.createToken(
                "user",
                Map.of("role", "USER")
        );

        Claims claims =
                jwtTokenProvider.getClaimsFromToken(token);

        assertNotNull(claims);

        assertTrue(
                jwtTokenProvider.validateToken(token)
        );
    }

}