package com.surest.member_service.service;

import com.surest.member_service.dto.AuthRequest;
import com.surest.member_service.dto.AuthResponse;


public interface AuthService {

    AuthResponse generateToken(AuthRequest authRequest);
}
