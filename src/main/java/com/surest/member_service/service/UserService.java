package com.surest.member_service.service;

import com.surest.member_service.dto.UserRequest;
import com.surest.member_service.dto.UserResponse;

public interface UserService {

    UserResponse registerUser(UserRequest request) throws Exception;
}
