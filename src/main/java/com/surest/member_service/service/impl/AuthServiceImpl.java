package com.surest.member_service.service.impl;

import com.surest.member_service.dto.AuthRequest;
import com.surest.member_service.dto.AuthResponse;
import com.surest.member_service.service.AuthService;
import com.surest.member_service.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    @Override
    public AuthResponse generateToken(AuthRequest authRequest) {
        log.info("Authenticating user: {}", authRequest.getUsername());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                )
        );
        log.info("Authentication successful for user: {}", authRequest.getUsername());

        String token = jwtUtil.generateToken(authRequest.getUsername());
        log.info("Generated JWT token for user: {}", authRequest.getUsername());

        return AuthResponse.builder().token(token).build();
    }
}
