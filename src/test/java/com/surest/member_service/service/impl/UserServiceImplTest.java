package com.surest.member_service.service.impl;

import com.surest.member_service.dto.UserRequest;
import com.surest.member_service.dto.UserResponse;
import com.surest.member_service.entities.RoleEntity;
import com.surest.member_service.entities.UserEntity;
import com.surest.member_service.exception.UserException;
import com.surest.member_service.repository.RoleRepository;
import com.surest.member_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_ShouldSaveUser_WhenRoleExists() throws Exception {
        // Arrange
        UserRequest request = UserRequest.builder().username("john_doe").password("password123").role("ROLE_ADMIN").build();
        RoleEntity existingRole = RoleEntity.builder().id(UUID.randomUUID()).name("ROLE_ADMIN").build();
        when(userRepository.findByUserName("john_doe")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(existingRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(i -> i.getArgument(0));
        UserResponse response = userService.registerUser(request);
        assertThat(response.getMessage()).isEqualTo("User registered successfully with role: ROLE_ADMIN");
        verify(userRepository).save(any(UserEntity.class));
        verify(roleRepository, never()).save(any(RoleEntity.class));
    }

    @Test
    void registerUser_ShouldCreateNewRole_WhenRoleDoesNotExist() throws Exception {
        UserRequest request = UserRequest.builder().username("alice").password("mypassword").role("ROLE_MANAGER").build();
        when(userRepository.findByUserName("alice")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_MANAGER")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("mypassword")).thenReturn("encodedPwd");
        RoleEntity newRole = RoleEntity.builder().id(UUID.randomUUID()).name("ROLE_MANAGER").build();
        when(roleRepository.save(any(RoleEntity.class))).thenReturn(newRole);
        when(userRepository.save(any(UserEntity.class))).thenAnswer(i -> i.getArgument(0));
        UserResponse response = userService.registerUser(request);
        assertThat(response.getMessage())
                .isEqualTo("User registered successfully with role: ROLE_MANAGER");
        verify(roleRepository).save(any(RoleEntity.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameAlreadyExists() {
        // Arrange
        UserRequest request = UserRequest.builder().username("john_doe").password("password123").role("ROLE_USER").build();
        when(userRepository.findByUserName("john_doe"))
                .thenReturn(Optional.of(UserEntity.builder()
                        .userId(UUID.randomUUID())
                        .userName("john_doe")
                        .build()));
        UserException ex = assertThrows(UserException.class,
                () -> userService.registerUser(request));
        assertThat(ex.getMessage()).isEqualTo("Username already exists");
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void registerUser_ShouldUseDefaultRole_WhenRoleIsNull() throws Exception {
        UserRequest request = UserRequest.builder().username("new_user").password("pass123").role(null).build();
        when(userRepository.findByUserName("new_user")).thenReturn(Optional.empty());
        when(roleRepository.findByName(null)).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");
        RoleEntity defaultRole = RoleEntity.builder().id(UUID.randomUUID()).name("ROLE_USER").build();
        when(roleRepository.save(any(RoleEntity.class))).thenReturn(defaultRole);
        when(userRepository.save(any(UserEntity.class))).thenAnswer(i -> i.getArgument(0));
        UserResponse response = userService.registerUser(request);
        assertThat(response.getMessage())
                .isEqualTo("User registered successfully with role: ROLE_USER");
        verify(roleRepository).save(any(RoleEntity.class));
    }
}
