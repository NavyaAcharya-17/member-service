package com.surest.member_service.service;

import com.surest.member_service.dto.UserRequest;
import com.surest.member_service.dto.UserResponse;
import com.surest.member_service.exception.UserException;

public interface UserService {

    UserResponse registerUser(UserRequest request) throws UserException;
}
