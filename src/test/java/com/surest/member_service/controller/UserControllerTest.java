package com.surest.member_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surest.member_service.dto.UserRequest;
import com.surest.member_service.dto.UserResponse;
import com.surest.member_service.exception.ResourceAlreadyExistsException;
import com.surest.member_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturn201WhenRegisterUserValid() throws Exception {
        UserRequest request = UserRequest.builder().username("john_doe").password("password123").roles(Set.of("USER")).build();
        UUID userId = UUID.randomUUID();
        UserResponse response = UserResponse.builder().userId(userId).username("john_doe").build();
        Mockito.when(userService.registerUser(Mockito.any(UserRequest.class))).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("john_doe"));
    }

    @Test
    void shouldReturn400WhenUserRequestInvalid()throws Exception {
        UserRequest invalidRequest = new UserRequest(); // missing all required fields

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value("Input validation failed"))
                .andExpect(jsonPath("$.details.username").value("User name is required"))
                .andExpect(jsonPath("$.details.password").value("Password is required"))
                .andExpect(jsonPath("$.details.roles").value("At least one role is required"));
    }

    @Test
    void shouldReturn409WhenUserAlreadyExists() throws Exception {
        UserRequest request = UserRequest.builder().username("existinguser").password("password123").roles(Set.of("USER")).build();

        Mockito.when(userService.registerUser(Mockito.any(UserRequest.class)))
                .thenThrow(new ResourceAlreadyExistsException());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("CONFLICT"))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Resource already exists"));
    }

    @Test
    void shouldReturn500WhenUnexpectedErrorOccurs()throws Exception {
        UserRequest request = UserRequest.builder().username("testuser").password("password123").roles(Set.of("USER")).build();

        Mockito.when(userService.registerUser(Mockito.any(UserRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Unexpected error"));
    }
}
