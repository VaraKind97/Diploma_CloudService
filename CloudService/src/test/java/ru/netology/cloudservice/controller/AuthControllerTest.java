package ru.netology.cloudservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.cloudservice.dto.request.AuthRequest;
import ru.netology.cloudservice.service.AuthService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void loginSuccess() throws Exception {

        AuthRequest request =
                new AuthRequest("user", "password");

        when(authService.login("user", "password"))
                .thenReturn("jwt-token");

        mockMvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.auth-token")
                        .value("jwt-token"));
    }

    @Test
    void logoutSuccess() throws Exception {

        mockMvc.perform(
                        post("/logout")
                                .header("auth-token", "Bearer token")
                )
                .andExpect(status().isOk());

        verify(authService)
                .invalidateToken("Bearer token");
    }

}
