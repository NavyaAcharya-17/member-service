package com.surest.member_service.service.impl;

import com.surest.member_service.dto.RegisterRequest;
import com.surest.member_service.entities.RoleEntity;
import com.surest.member_service.entities.UserEntity;
import com.surest.member_service.repository.RoleRepository;
import com.surest.member_service.repository.UserRepository;
import com.surest.member_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String registerUser(RegisterRequest request) {
        if (userRepository.findByUserName(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        RoleEntity role = roleRepository.findByName(request.getRole())
                .orElseGet(() -> roleRepository.save(RoleEntity.builder().name(request.getRole() != null ? request.getRole() : "ROLE_USER").build()));
        UserEntity user = UserEntity.builder().userName(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword())).role(role).build();
        userRepository.save(user);
        return "User registered successfully with role: " + role.getName();
    }
}
