package com.surest.member_service.controller;

import com.surest.member_service.dto.AuthRequest;
import com.surest.member_service.dto.AuthResponse;
import com.surest.member_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        log.info("Received login request for username: {}", authRequest.getUsername());
        AuthResponse response = authService.generateToken(authRequest);
        log.info("Login successful for username: {}", authRequest.getUsername());
        return ResponseEntity.ok(response);
    }
}
