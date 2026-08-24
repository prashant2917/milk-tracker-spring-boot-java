package com.swarajya.milktracker.service;

import com.swarajya.milktracker.dto.request.UpdateUserSettingsRequest;
import com.swarajya.milktracker.dto.response.UserSettingsResponse;
import com.swarajya.milktracker.entity.User;
import com.swarajya.milktracker.entity.UserSettings;
import com.swarajya.milktracker.exception.UserNotFoundException;
import com.swarajya.milktracker.repository.UserRepository;
import com.swarajya.milktracker.repository.UserSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserSettingsService {

    private final UserSettingsRepository settingsRepository;
    private final UserRepository userRepository;

    public UserSettingsService(
            UserSettingsRepository settingsRepository,
            UserRepository userRepository
    ) {
        this.settingsRepository = settingsRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserSettingsResponse getSettings(
            String email
    ) {

        User user = getUserByEmail(email);

        UserSettings settings =
                settingsRepository
                        .findByUserUuid(user.getUuid())
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "User settings not found"
                                )
                        );

        return mapToResponse(settings);
    }

    @Transactional
    public UserSettingsResponse updateSettings(
            String email,
            UpdateUserSettingsRequest request
    ) {

        User user = getUserByEmail(email);

        UserSettings settings =
                settingsRepository
                        .findByUserUuid(user.getUuid())
                        .orElseGet(() -> {

                            UserSettings newSettings =
                                    new UserSettings();

                            newSettings.setUser(user);

                            return newSettings;
                        });

        settings.setDefaultPricePerLiter(
                request.getDefaultPricePerLiter()
        );

        UserSettings savedSettings =
                settingsRepository.save(settings);

        return mapToResponse(savedSettings);
    }

    private User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        )
                );
    }

    private UserSettingsResponse mapToResponse(
            UserSettings settings
    ) {

        return new UserSettingsResponse(
                settings.getDefaultPricePerLiter()
        );
    }
}