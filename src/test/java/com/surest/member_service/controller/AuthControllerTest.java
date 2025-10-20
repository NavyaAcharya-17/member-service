package com.surest.member_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surest.member_service.dto.AuthRequest;
import com.surest.member_service.dto.AuthResponse;
import com.surest.member_service.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testLoginSuccess() throws Exception {
        // Arrange
        AuthRequest authRequest = new AuthRequest("john_doe", "password123");
        AuthResponse authResponse = new AuthResponse("mock-jwt-token");
        Mockito.when(authService.generateToken(any(AuthRequest.class)))
                .thenReturn(authResponse);
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
    }

    @Test
    void testLoginValidationFailure() throws Exception {
        AuthRequest authRequest = new AuthRequest("john_doe", "wrong_password");

        Mockito.when(authService.generateToken(any(AuthRequest.class)))
                .thenThrow(new RuntimeException("Invalid username or password"));
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isInternalServerError()) // or .isUnauthorized() if you handle 401 in GlobalExceptionHandler
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }
}
