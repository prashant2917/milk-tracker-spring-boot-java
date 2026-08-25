package com.swarajya.milktracker.repository;

import com.swarajya.milktracker.entity.MilkEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MilkEntryRepository
        extends JpaRepository<MilkEntry, UUID> {

    Optional<MilkEntry> findByUserUuidAndDate(
            UUID userUuid,
            LocalDate date
    );

    List<MilkEntry> findByUserUuidAndDateBetweenOrderByDateAsc(
            UUID userUuid,
            LocalDate startDate,
            LocalDate endDate
    );

    boolean existsByUserUuidAndDate(
            UUID userUuid,
            LocalDate date
    );

    void deleteByUserUuidAndDate(
            UUID userUuid,
            LocalDate date
    );
}