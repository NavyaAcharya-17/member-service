package com.surest.member_service.service.impl;

import com.surest.member_service.entities.UserEntity;
import com.surest.member_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
        String username = "john_doe";
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(username);
        userEntity.setPasswordHash("password123");
        when(userRepository.findByUserName(username)).thenReturn(Optional.of(userEntity));
        UserDetails result = customUserDetailsService.loadUserByUsername(username);
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals("password123", result.getPassword());
        verify(userRepository, times(1)).findByUserName(username);
    }

    @Test
    void loadUserByUsernameShouldThrowExceptionWhenUserDoesNotExist() {
        String username = "non_existing_user";
        when(userRepository.findByUserName(username)).thenReturn(Optional.empty());
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername(username));
        assertEquals("Username not found", exception.getMessage());
        verify(userRepository, times(1)).findByUserName(username);
    }
}
