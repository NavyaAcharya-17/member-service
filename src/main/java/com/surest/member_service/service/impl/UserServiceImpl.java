package com.surest.member_service.service.impl;

import com.surest.member_service.dto.UserRequest;
import com.surest.member_service.dto.UserResponse;
import com.surest.member_service.entities.RoleEntity;
import com.surest.member_service.entities.UserEntity;
import com.surest.member_service.repository.RoleRepository;
import com.surest.member_service.repository.UserRepository;
import com.surest.member_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse registerUser(UserRequest request) throws Exception {
        if (userRepository.findByUserName(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        RoleEntity role = roleRepository.findByName(request.getRole())
                .orElseGet(() -> roleRepository.save(RoleEntity.builder().name(request.getRole() != null ? request.getRole() : "ROLE_USER").build()));
        UserEntity user = UserEntity.builder().userName(request.getUsername()).passwordHash(passwordEncoder.encode(request.getPassword())).role(role).build();
        userRepository.save(user);
        return UserResponse.builder().message("User registered successfully with role: " + role.getName()).build();
    }
}
