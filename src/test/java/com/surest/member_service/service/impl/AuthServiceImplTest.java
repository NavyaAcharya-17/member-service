package com.surest.member_service.service.impl;

import com.surest.member_service.dto.AuthRequest;
import com.surest.member_service.dto.AuthResponse;
import com.surest.member_service.util.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateTokenShouldReturnTokenWhenAuthenticationIsSuccessful() throws Exception {
        AuthRequest authRequest = AuthRequest.builder().username("john_doe").password("password123").build();
        String mockToken = "mock-jwt-token";
        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(jwtUtil.generateToken("john_doe")).thenReturn(mockToken);
        AuthResponse response = authService.generateToken(authRequest);
        assertThat(response.getToken()).isEqualTo(mockToken);
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, times(1)).generateToken("john_doe");
    }

    @Test
    void generateTokenShouldThrowExceptionWhenInvalidCredentials() {
        AuthRequest authRequest = AuthRequest.builder().username("john_doe").password("wrong_password").build();
        doThrow(new BadCredentialsException("Invalid username or password"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.generateToken(authRequest)
        );
        assertThat(exception.getMessage()).isEqualTo("Invalid username or password");
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void generateTokenShouldThrowExceptionWhenUnexpectedErrorOccurs() {
        AuthRequest authRequest = AuthRequest.builder().username("jane_doe").password("password123").build();
        doThrow(new RuntimeException("Authentication system failure"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        Exception exception = assertThrows(
                Exception.class,
                () -> authService.generateToken(authRequest)
        );
        assertThat(exception.getMessage()).isEqualTo("Authentication system failure");
        verify(jwtUtil, never()).generateToken(anyString());
    }
}
