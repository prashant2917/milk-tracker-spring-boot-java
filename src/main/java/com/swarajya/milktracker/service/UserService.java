package com.swarajya.milktracker.service;

import com.swarajya.milktracker.dto.request.CreateUserRequest;
import com.swarajya.milktracker.dto.request.LoginRequest;
import com.swarajya.milktracker.dto.response.LoginResponse;
import com.swarajya.milktracker.dto.response.UserResponse;
import com.swarajya.milktracker.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(UUID id);
    UserResponse getUserByEmail(String email);
    LoginResponse login(LoginRequest request);
}
