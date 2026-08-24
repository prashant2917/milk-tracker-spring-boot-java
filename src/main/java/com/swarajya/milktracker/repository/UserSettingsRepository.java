package com.swarajya.milktracker.repository;

import com.swarajya.milktracker.entity.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserSettingsRepository
        extends JpaRepository<UserSettings, UUID> {

    Optional<UserSettings> findByUserUuid(UUID userUuid);
}
