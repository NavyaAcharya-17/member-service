package com.surest.member_service.service.impl;

import com.surest.member_service.dto.UserRequest;
import com.surest.member_service.dto.UserResponse;
import com.surest.member_service.entities.RoleEntity;
import com.surest.member_service.entities.UserEntity;
import com.surest.member_service.exception.ResourceAlreadyExistsException;
import com.surest.member_service.repository.RoleRepository;
import com.surest.member_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        userRequest = UserRequest.builder()
                .username("testuser")
                .password("password123")
                .roles(Set.of("USER"))
                .build();
    }

    @Test
    void testRegisterUserWhenSuccess() {
        // Arrange: Setup mocks
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        RoleEntity userRole = RoleEntity.builder().name("USER").build();
        when(roleRepository.findByNameIn(Set.of("USER"))).thenReturn(Set.of(userRole));
        UserEntity savedUser = UserEntity.builder().userName("testuser").passwordHash("encodedPassword").roles(Set.of(userRole)).build();
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        // Act: Call the service
        UserResponse response = userService.registerUser(userRequest);

        // Assert: Verify result
        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        verify(userRepository).save(any(UserEntity.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void testRegisterUserAlreadyExists() {
        // Arrange: Simulate existing user
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(new UserEntity()));

        // Act & Assert: Expect exception
        assertThrows(ResourceAlreadyExistsException.class, () -> userService.registerUser(userRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegisterUserInvalidRoles(){
        // Arrange: No roles found
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.empty());
        when(roleRepository.findByNameIn(Set.of("USER"))).thenReturn(Set.of());

        // Act & Assert: Expect exception
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(userRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testPasswordIsEncoded() {
        // Arrange: Setup mocks
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        RoleEntity userRole = new RoleEntity();
        userRole.setName("USER");
        when(roleRepository.findByNameIn(Set.of("USER"))).thenReturn(Set.of(userRole));
        when(userRepository.save(any(UserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act: Call the service
        UserResponse response = userService.registerUser(userRequest);

        // Assert: Verify password encoding
        assertEquals("testuser", response.getUsername());
        verify(passwordEncoder, times(1)).encode("password123");
    }
}
