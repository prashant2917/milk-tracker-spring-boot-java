package com.swarajya.milktracker.controller;

import com.swarajya.milktracker.dto.request.UpdateUserSettingsRequest;
import com.swarajya.milktracker.dto.response.UserSettingsResponse;
import com.swarajya.milktracker.service.UserSettingsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final UserSettingsService settingsService;

    public SettingsController(
            UserSettingsService settingsService
    ) {
        this.settingsService = settingsService;
    }

    @GetMapping
    public ResponseEntity<UserSettingsResponse> getSettings(
            Authentication authentication
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                settingsService.getSettings(email)
        );
    }

    @PutMapping
    public ResponseEntity<UserSettingsResponse> updateSettings(
            Authentication authentication,
            @Valid @RequestBody
            UpdateUserSettingsRequest request
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                settingsService.updateSettings(
                        email,
                        request
                )
        );
    }
}