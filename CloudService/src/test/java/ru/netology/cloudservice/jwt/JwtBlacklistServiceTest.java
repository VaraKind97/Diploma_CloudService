package ru.netology.cloudservice.jwt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtBlacklistServiceTest {

    private final JwtBlacklistService blacklistService =
            new JwtBlacklistService();

    @Test
    void tokenShouldNotBeBlacklistedInitially() {

        assertFalse(
                blacklistService.isBlacklisted("token")
        );
    }

    @Test
    void blacklistToken() {

        blacklistService.blacklist("token");

        assertTrue(
                blacklistService.isBlacklisted("token")
        );
    }

    @Test
    void blacklistSeveralTokens() {

        blacklistService.blacklist("token1");
        blacklistService.blacklist("token2");
        blacklistService.blacklist("token3");

        assertTrue(
                blacklistService.isBlacklisted("token1")
        );

        assertTrue(
                blacklistService.isBlacklisted("token2")
        );

        assertTrue(
                blacklistService.isBlacklisted("token3")
        );

        assertFalse(
                blacklistService.isBlacklisted("unknown")
        );
    }

    @Test
    void blacklistSameTokenTwice() {

        blacklistService.blacklist("token");
        blacklistService.blacklist("token");

        assertTrue(
                blacklistService.isBlacklisted("token")
        );
    }

}
