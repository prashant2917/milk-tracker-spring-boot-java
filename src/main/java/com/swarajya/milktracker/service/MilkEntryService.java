package com.swarajya.milktracker.service;

import com.swarajya.milktracker.dto.request.CreateMilkEntryRequest;
import com.swarajya.milktracker.dto.response.MilkEntryResponse;
import com.swarajya.milktracker.dto.response.MonthlyMilkSummaryResponse;
import com.swarajya.milktracker.entity.MilkEntry;
import com.swarajya.milktracker.entity.User;
import com.swarajya.milktracker.entity.UserSettings;
import com.swarajya.milktracker.exception.InvalidMilkQuantityException;
import com.swarajya.milktracker.exception.MilkEntryNotFoundException;
import com.swarajya.milktracker.exception.UserNotFoundException;
import com.swarajya.milktracker.exception.UserSettingsNotFoundException;
import com.swarajya.milktracker.repository.MilkEntryRepository;
import com.swarajya.milktracker.repository.UserRepository;
import com.swarajya.milktracker.repository.UserSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class MilkEntryService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private static final BigDecimal MIN_MILK_QUANTITY =
            BigDecimal.valueOf(0.5);

    private final MilkEntryRepository milkEntryRepository;
    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;

    public MilkEntryService(
            MilkEntryRepository milkEntryRepository,
            UserRepository userRepository,
            UserSettingsRepository userSettingsRepository
    ) {
        this.milkEntryRepository = milkEntryRepository;
        this.userRepository = userRepository;
        this.userSettingsRepository = userSettingsRepository;
    }

    /**
     * Create or update a milk entry for the authenticated user.
     * <p>
     * PUT /api/milk/{date}
     */
    @Transactional
    public MilkEntryResponse createOrUpdateEntry(
            String email,
            LocalDate date,
            CreateMilkEntryRequest request
    ) {

        User user = getUserByEmail(email);

        validateMilkQuantities(
                request.getMorningQty(),
                request.getEveningQty()
        );

        MilkEntry entry =
                milkEntryRepository
                        .findByUserUuidAndDate(
                                user.getUuid(),
                                date
                        )
                        .orElse(null);

        if (entry == null) {

            entry = new MilkEntry();

            entry.setUser(user);
            entry.setDate(date);

            /*
             * Price is obtained from user settings only
             * when creating a new entry.
             */
            UserSettings settings =
                    getUserSettings(user.getUuid());

            entry.setPricePerLiter(
                    settings.getDefaultPricePerLiter()
            );
        }

        /*
         * When updating an existing entry,
         * its original pricePerLiter is preserved.
         */
        entry.setMorningQty(
                request.getMorningQty()
        );

        entry.setEveningQty(
                request.getEveningQty()
        );

        MilkEntry savedEntry =
                milkEntryRepository.save(entry);

        return mapToResponse(savedEntry);
    }

    /**
     * Get milk entry for a particular date.
     */
    @Transactional(readOnly = true)
    public MilkEntryResponse getEntryForDate(
            String email,
            LocalDate date
    ) {

        User user = getUserByEmail(email);

        MilkEntry entry =
                milkEntryRepository
                        .findByUserUuidAndDate(
                                user.getUuid(),
                                date
                        )
                        .orElseThrow(() ->
                                new MilkEntryNotFoundException(
                                        "Milk entry not found for date: "
                                                + date
                                )
                        );

        return mapToResponse(entry);
    }

    /**
     * Get all milk entries for a month.
     */
    @Transactional(readOnly = true)
    public List<MilkEntryResponse> getEntriesForMonth(
            String email,
            int year,
            int month
    ) {

        User user = getUserByEmail(email);

        LocalDate startDate =
                LocalDate.of(year, month, 1);

        LocalDate endDate =
                startDate.withDayOfMonth(
                        startDate.lengthOfMonth()
                );

        List<MilkEntry> entries =
                milkEntryRepository
                        .findByUserUuidAndDateBetweenOrderByDateAsc(
                                user.getUuid(),
                                startDate,
                                endDate
                        );

        return entries.stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Delete milk entry for a particular date.
     */
    @Transactional
    public void deleteEntry(
            String email,
            LocalDate date
    ) {

        User user = getUserByEmail(email);

        MilkEntry entry =
                milkEntryRepository
                        .findByUserUuidAndDate(
                                user.getUuid(),
                                date
                        )
                        .orElseThrow(() ->
                                new MilkEntryNotFoundException(
                                        "Milk entry not found for date: "
                                                + date
                                )
                        );

        milkEntryRepository.delete(entry);
    }

    /**
     * Business validation:
     * <p>
     * 0 is valid for either morning or evening,
     * but the other quantity must be >= 0.5.
     * <p>
     * A non-zero quantity must be >= 0.5.
     */
    private void validateMilkQuantities(
            BigDecimal morningQty,
            BigDecimal eveningQty
    ) {

        boolean morningZero =
                morningQty.compareTo(ZERO) == 0;

        boolean eveningZero =
                eveningQty.compareTo(ZERO) == 0;

        // Neither morning nor evening has milk.
        if (morningZero && eveningZero) {
            throw new InvalidMilkQuantityException(
                    "At least one milk quantity must be at least 0.5 liter"
            );
        }

        // Morning quantity is invalid.
        if (!morningZero &&
                morningQty.compareTo(MIN_MILK_QUANTITY) < 0) {

            throw new InvalidMilkQuantityException(
                    "Morning quantity must be 0 or at least 0.5 liter"
            );
        }

        // Evening quantity is invalid.
        if (!eveningZero &&
                eveningQty.compareTo(MIN_MILK_QUANTITY) < 0) {

            throw new InvalidMilkQuantityException(
                    "Evening quantity must be 0 or at least 0.5 liter"
            );
        }
    }

    private User getUserByEmail(String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        )
                );
    }

    private UserSettings getUserSettings(
            UUID userUuid
    ) {

        return userSettingsRepository
                .findByUserUuid(userUuid)
                .orElseThrow(() ->
                        new UserSettingsNotFoundException(
                                "User settings not found"
                        )
                );
    }

    private MilkEntryResponse mapToResponse(
            MilkEntry entry
    ) {

        BigDecimal totalQty =
                entry.getMorningQty()
                        .add(entry.getEveningQty());

        BigDecimal totalAmount =
                totalQty
                        .multiply(entry.getPricePerLiter())
                        .setScale(2, RoundingMode.HALF_UP);

        MilkEntryResponse response =
                new MilkEntryResponse();

        response.setUuid(entry.getUuid());
        response.setDate(entry.getDate());
        response.setMorningQty(entry.getMorningQty());
        response.setEveningQty(entry.getEveningQty());
        response.setTotalQty(totalQty);
        response.setPricePerLiter(entry.getPricePerLiter());
        response.setTotalAmount(totalAmount);
        response.setCreatedAt(entry.getCreatedAt());
        response.setUpdatedAt(entry.getUpdatedAt());

        return response;
    }

    @Transactional(readOnly = true)
    public MonthlyMilkSummaryResponse getMonthlySummary(
            String email,
            int year,
            int month
    ) {

        if (month < 1 || month > 12) {
            throw new IllegalArgumentException(
                    "Month must be between 1 and 12"
            );
        }
        User user = getUserByEmail(email);

        LocalDate startDate =
                LocalDate.of(year, month, 1);

        LocalDate endDate =
                startDate.withDayOfMonth(
                        startDate.lengthOfMonth()
                );

        List<MilkEntry> entries =
                milkEntryRepository
                        .findByUserUuidAndDateBetweenOrderByDateAsc(
                                user.getUuid(),
                                startDate,
                                endDate
                        );

        BigDecimal totalMorningQty =
                entries.stream()
                        .map(MilkEntry::getMorningQty)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal totalEveningQty =
                entries.stream()
                        .map(MilkEntry::getEveningQty)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal totalQty =
                totalMorningQty.add(totalEveningQty);

        BigDecimal totalAmount =
                entries.stream()
                        .map(entry ->
                                entry.getMorningQty()
                                        .add(entry.getEveningQty())
                                        .multiply(entry.getPricePerLiter())
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        )
                        .setScale(2, RoundingMode.HALF_UP);

        MonthlyMilkSummaryResponse response =
                new MonthlyMilkSummaryResponse();

        response.setYear(year);
        response.setMonth(month);
        response.setTotalDays(entries.size());
        response.setTotalMorningQty(totalMorningQty);
        response.setTotalEveningQty(totalEveningQty);
        response.setTotalQty(totalQty);
        response.setTotalAmount(totalAmount);

        return response;
    }
}