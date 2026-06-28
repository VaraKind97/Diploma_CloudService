package ru.netology.cloudservice.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.netology.cloudservice.exceptions.AuthException;
import ru.netology.cloudservice.jwt.JwtBlacklistService;
import ru.netology.cloudservice.jwt.JwtTokenProvider;
import ru.netology.cloudservice.model.UserEntity;
import ru.netology.cloudservice.repository.UserRepository;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository repo;

    @Mock
    private PasswordEncoder enc;

    @Mock
    private JwtTokenProvider jwt;

    @Mock
    private JwtBlacklistService blacklistService;

    @InjectMocks
    private AuthService authService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = UserEntity.builder()
                .id(1L)
                .username("user")
                .password("encodedPassword")
                .build();
    }

    @Test
    void loginSuccess() {

        when(repo.findByUsername("user"))
                .thenReturn(Optional.of(user));

        when(enc.matches(
                "123",
                user.getPassword()
        )).thenReturn(true);

        when(jwt.createToken(
                eq("user"),
                any(Map.class)
        )).thenReturn("token");

        String token = authService.login("user", "123");

        assertEquals("token", token);
    }

    @Test
    void loginUserNotFound() {

        when(repo.findByUsername("user"))
                .thenReturn(Optional.empty());

        AuthException ex =
                assertThrows(
                        AuthException.class,
                        () -> authService.login(
                                "user",
                                "123"
                        )
                );

        assertEquals(
                "User not found",
                ex.getMessage()
        );
    }

    @Test
    void loginWrongPassword() {

        when(repo.findByUsername("user"))
                .thenReturn(Optional.of(user));

        when(enc.matches(
                anyString(),
                anyString()
        )).thenReturn(false);

        AuthException ex =
                assertThrows(
                        AuthException.class,
                        () -> authService.login(
                                "user",
                                "123"
                        )
                );

        assertEquals(
                "Invalid password",
                ex.getMessage()
        );
    }

    @Test
    void registerSuccess() {

        when(repo.findByUsername("user"))
                .thenReturn(Optional.empty());

        when(enc.encode("123"))
                .thenReturn("encoded");

        when(repo.save(any(UserEntity.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        UserEntity result =
                authService.register(
                        "user",
                        "123"
                );

        assertEquals(
                "user",
                result.getUsername()
        );

        assertEquals(
                "encoded",
                result.getPassword()
        );
    }

    @Test
    void registerUserAlreadyExists() {

        when(repo.findByUsername("user"))
                .thenReturn(Optional.of(user));

        AuthException ex =
                assertThrows(
                        AuthException.class,
                        () -> authService.register(
                                "user",
                                "123"
                        )
                );

        assertEquals(
                "User already exists",
                ex.getMessage()
        );
    }

    @Test
    void invalidateToken() {

        authService.invalidateToken(
                "Bearer token"
        );

        verify(blacklistService)
                .blacklist("token");
    }

    @Test
    void invalidateTokenWithoutBearer() {

        authService.invalidateToken(
                "token"
        );

        verify(blacklistService)
                .blacklist("token");
    }

    @Test
    void getUserFromTokenWithBearerPrefix() {

        Claims claims = mock(Claims.class);

        when(jwt.getClaimsFromToken("token"))
                .thenReturn(claims);

        when(claims.getSubject())
                .thenReturn("user");

        when(repo.findByUsername("user"))
                .thenReturn(Optional.of(user));

        UserEntity result =
                authService.getUserFromToken(
                        "Bearer token"
                );

        assertEquals(
                "user",
                result.getUsername()
        );

        verify(jwt)
                .getClaimsFromToken("token");

        verify(repo)
                .findByUsername("user");
    }

    @Test
    void getUserFromTokenWithoutBearerPrefix() {

        Claims claims = mock(Claims.class);

        when(jwt.getClaimsFromToken("token"))
                .thenReturn(claims);

        when(claims.getSubject())
                .thenReturn("user");

        when(repo.findByUsername("user"))
                .thenReturn(Optional.of(user));

        UserEntity result =
                authService.getUserFromToken(
                        "token"
                );

        assertEquals(
                "user",
                result.getUsername()
        );

        verify(jwt)
                .getClaimsFromToken("token");
    }

    @Test
    void getUserFromTokenUserNotFound() {

        Claims claims = mock(Claims.class);

        when(jwt.getClaimsFromToken("token"))
                .thenReturn(claims);

        when(claims.getSubject())
                .thenReturn("unknown");

        when(repo.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        AuthException ex =
                assertThrows(
                        AuthException.class,
                        () -> authService.getUserFromToken(
                                "Bearer token"
                        )
                );

        assertEquals(
                "User not found",
                ex.getMessage()
        );
    }

}