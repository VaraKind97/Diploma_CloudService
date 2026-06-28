package ru.netology.cloudservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.cloudservice.dto.request.AuthRequest;
import ru.netology.cloudservice.dto.response.AuthResponse;
import ru.netology.cloudservice.model.UserEntity;
import ru.netology.cloudservice.service.AuthService;

@RestController
public class AuthController {

    private static final Logger log =
            LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserEntity> register(@RequestBody AuthRequest request) {

        log.info(
                "Registration request for user '{}'",
                request.getLogin()
        );

        UserEntity user = authService.register(
                request.getLogin(),
                request.getPassword()
        );

        log.info(
                "User '{}' successfully registered",
                request.getLogin()
        );

        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {

        log.info(
                "Login request for user '{}'",
                request.getLogin()
        );

        String token = authService.login(
                request.getLogin(),
                request.getPassword()
        );

        log.info(
                "User '{}' successfully authenticated",
                request.getLogin()
        );

        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("auth-token") String token) {

        log.info("Logout request");

        authService.invalidateToken(token);

        log.info("User successfully logged out");

        return ResponseEntity.ok().build();
    }
}