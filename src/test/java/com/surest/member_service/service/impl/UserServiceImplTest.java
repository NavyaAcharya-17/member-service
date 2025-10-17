package com.surest.member_service.service.impl;

import com.surest.member_service.dto.UserRequest;
import com.surest.member_service.dto.UserResponse;
import com.surest.member_service.entities.RoleEntity;
import com.surest.member_service.entities.UserEntity;
import com.surest.member_service.repository.RoleRepository;
import com.surest.member_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
    void testRegisterUserSuccessWithExistingRole() {
        UserRequest request = new UserRequest();
        request.setUsername("john");
        request.setPassword("password");
        request.setRole("ROLE_ADMIN");
        RoleEntity existingRole = RoleEntity.builder().name("ROLE_ADMIN").build();

        when(userRepository.findByUserName("john")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(existingRole));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        UserResponse response = userService.registerUser(request);

        assertNotNull(response);
        assertEquals("User registered successfully with role: ROLE_ADMIN", response.getMessage());

        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userCaptor.capture());

        UserEntity savedUser = userCaptor.getValue();
        assertEquals("john", savedUser.getUsername());
        assertEquals("encodedPassword", savedUser.getPasswordHash());
        assertEquals(existingRole, savedUser.getRole());
    }

    @Test
    void testRegisterUserThrowsExceptionWhenUsernameExists() {
        UserRequest request = new UserRequest();
        request.setUsername("john");
        request.setPassword("password");

        when(userRepository.findByUserName("john")).thenReturn(Optional.of(new UserEntity()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(request);
        });

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void registerUser_ShouldUseDefaultRole_WhenRoleIsNull()  {
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
