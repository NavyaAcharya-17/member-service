package com.surest.member_service.service.impl;

import com.surest.member_service.dto.UserRequest;
import com.surest.member_service.dto.UserResponse;
import com.surest.member_service.entities.RoleEntity;
import com.surest.member_service.entities.UserEntity;
import com.surest.member_service.exception.ResourceAlreadyExistsException;
import com.surest.member_service.repository.RoleRepository;
import com.surest.member_service.repository.UserRepository;
import com.surest.member_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse registerUser(UserRequest request) {
        if (userRepository.findByUserName(request.getUsername()).isPresent()) {
            throw new ResourceAlreadyExistsException();
        }
        Set<RoleEntity> roles = roleRepository.findByNameIn(request.getRoles());
        if (roles.isEmpty()) {
            throw new IllegalArgumentException("At least one valid role must be provided");
        }
        UserEntity user = request.toEntity(passwordEncoder.encode(request.getPassword()), roles);
        return UserResponse.fromEntity(userRepository.save(user));
    }
}