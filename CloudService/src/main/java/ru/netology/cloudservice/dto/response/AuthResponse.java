package ru.netology.cloudservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(
        @JsonProperty("auth-token")
        String authToken) {
}