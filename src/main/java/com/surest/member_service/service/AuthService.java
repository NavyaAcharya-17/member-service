package com.surest.member_service.service;

import com.surest.member_service.dto.RegisterRequest;

public interface AuthService {

    String registerUser(RegisterRequest request);
}
