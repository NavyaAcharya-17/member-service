package com.surest.member_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surest.member_service.dto.UserRequest;
import com.surest.member_service.dto.UserResponse;
import com.surest.member_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUser_ShouldReturnSuccess_WhenValidRequest() throws Exception {
        UserRequest request = UserRequest.builder().username("john_doe").password("password123").role("USER").build();
        UserResponse response = UserResponse.builder().message("User registered successfully").build();
        Mockito.when(userService.registerUser(Mockito.any(UserRequest.class))).thenReturn(response);
        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    void registerUser_ShouldReturn400_WhenUsernameMissing() throws Exception {
        UserRequest request = UserRequest.builder().username("").password("password123").role("USER").build();
        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.username").value("User name is required"));
    }

    void registerUser_ShouldReturn400_WhenPasswordMissing() throws Exception {
        UserRequest request = UserRequest.builder().username("john_doe").password("").role("USER").build();
        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.password").exists());
    }

    @Test
    void registerUser_ShouldReturn400_WhenRoleMissing() throws Exception {
        UserRequest request = UserRequest.builder().username("john_doe").password("password123").role("").build();
        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.role").exists());
    }

    @Test
    void registerUser_ShouldReturn500_WhenServiceThrowsException() throws Exception {
        UserRequest request = UserRequest.builder().username("john_doe").password("password123").role("USER").build();
        Mockito.when(userService.registerUser(Mockito.any(UserRequest.class)))
                .thenThrow(new RuntimeException("Internal server error"));
        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }
}
