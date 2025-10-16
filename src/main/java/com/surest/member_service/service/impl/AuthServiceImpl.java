package com.surest.member_service.service.impl;

import com.surest.member_service.dto.AuthRequest;
import com.surest.member_service.dto.AuthResponse;
import com.surest.member_service.dto.UserRequest;
import com.surest.member_service.dto.UserResponse;
import com.surest.member_service.entities.RoleEntity;
import com.surest.member_service.entities.UserEntity;
import com.surest.member_service.repository.RoleRepository;
import com.surest.member_service.repository.UserRepository;
import com.surest.member_service.service.AuthService;
import com.surest.member_service.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    @Override
    public AuthResponse generateToken(AuthRequest authRequest) throws Exception {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                )
        );
        String token = jwtUtil.generateToken(authRequest.getUsername());
        return AuthResponse.builder()
                .token(token)
                .build();
    }
}
