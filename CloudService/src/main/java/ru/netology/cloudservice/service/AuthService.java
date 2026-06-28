package ru.netology.cloudservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.cloudservice.exceptions.AuthException;
import ru.netology.cloudservice.jwt.JwtBlacklistService;
import ru.netology.cloudservice.jwt.JwtTokenProvider;
import ru.netology.cloudservice.model.UserEntity;
import ru.netology.cloudservice.repository.UserRepository;

import java.util.Map;

@Service
public class AuthService {
    private final UserRepository repo;
    private final PasswordEncoder enc;
    private final JwtTokenProvider jwt;
    private final JwtBlacklistService blacklistService;
    private static final Logger log =
            LoggerFactory.getLogger(AuthService.class);

    public AuthService(UserRepository repo,
                       PasswordEncoder enc,
                       JwtTokenProvider jwt,
                       JwtBlacklistService blacklistService) {
        this.repo = repo;
        this.enc = enc;
        this.jwt = jwt;
        this.blacklistService = blacklistService;
    }

    public String login(String username, String password) {

        log.info(
                "Authentication attempt for user '{}'",
                username
        );

        UserEntity user = repo.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User '{}' not found", username);
                    return new AuthException("User not found");
                });

        if (!enc.matches(password, user.getPassword())) {
            log.warn(
                    "Invalid password for user '{}'",
                    username
            );
            throw new AuthException("Invalid password");
        }

        log.info(
                "User '{}' successfully authenticated",
                username
        );

        return jwt.createToken(username, Map.of());
    }

    public UserEntity register(String username, String password) {

        log.info(
                "Registering user '{}'",
                username
        );

        if (repo.findByUsername(username).isPresent()) {
            log.warn(
                    "User '{}' already exists",
                    username
            );
            throw new AuthException("User already exists");
        }

        UserEntity user = repo.save(
                UserEntity.builder()
                        .username(username)
                        .password(enc.encode(password))
                        .build()
        );

        log.info(
                "User '{}' successfully registered",
                username
        );

        return user;
    }

    public UserEntity getUserFromToken(String auth) {

        String t = auth.startsWith("Bearer ")
                ? auth.substring(7)
                : auth;

        String username = jwt.getClaimsFromToken(t).getSubject();

        log.info(
                "Getting user '{}' from JWT",
                username
        );

        return repo.findByUsername(username)
                .orElseThrow(() -> new AuthException("User not found"));
    }

    public void invalidateToken(String token) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        blacklistService.blacklist(token);

        log.info("JWT token invalidated");
    }
}