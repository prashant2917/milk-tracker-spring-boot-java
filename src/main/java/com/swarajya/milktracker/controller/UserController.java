package com.swarajya.milktracker.controller;

import com.swarajya.milktracker.dto.response.UserResponse;
import com.swarajya.milktracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/currentUser")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {

        String email = authentication.getName();

        UserResponse user = userService.getUserByEmail(email);

        return ResponseEntity.ok(user);
    }
}