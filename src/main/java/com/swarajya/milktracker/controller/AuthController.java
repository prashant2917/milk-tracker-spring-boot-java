package com.swarajya.milktracker.controller;

import com.swarajya.milktracker.dto.request.CreateUserRequest;
import com.swarajya.milktracker.dto.request.LoginRequest;
import com.swarajya.milktracker.dto.request.LogoutRequest;
import com.swarajya.milktracker.dto.request.RefreshTokenRequest;
import com.swarajya.milktracker.dto.response.LoginResponse;
import com.swarajya.milktracker.dto.response.UserResponse;
import com.swarajya.milktracker.service.RefreshTokenService;
import com.swarajya.milktracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(UserService userService, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = userService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {

        LoginResponse response =
                userService.refreshToken(
                        request.getRefreshToken()
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody LogoutRequest request,
            Authentication authentication
    ) {

        refreshTokenService.logout(
                request.getRefreshToken(),
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }

}
