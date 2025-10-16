package com.surest.member_service.service;

import com.surest.member_service.dto.AuthRequest;
import com.surest.member_service.dto.AuthResponse;
import com.surest.member_service.dto.UserRequest;
import com.surest.member_service.dto.UserResponse;

public interface AuthService {

    AuthResponse generateToken(AuthRequest authRequest) throws Exception;
}
