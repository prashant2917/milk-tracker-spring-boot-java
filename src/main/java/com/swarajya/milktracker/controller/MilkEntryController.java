package com.swarajya.milktracker.controller;

import com.swarajya.milktracker.dto.request.CreateMilkEntryRequest;
import com.swarajya.milktracker.dto.response.MilkEntryResponse;
import com.swarajya.milktracker.dto.response.MonthlyMilkSummaryResponse;
import com.swarajya.milktracker.service.MilkEntryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/milk")
public class MilkEntryController {

    private final MilkEntryService milkEntryService;

    public MilkEntryController(
            MilkEntryService milkEntryService
    ) {
        this.milkEntryService = milkEntryService;
    }

    @PutMapping("/{date}")
    public ResponseEntity<MilkEntryResponse> createOrUpdateEntry(
            Authentication authentication,
            @PathVariable LocalDate date,
            @Valid @RequestBody CreateMilkEntryRequest request
    ) {

        String email = authentication.getName();

        MilkEntryResponse response =
                milkEntryService.createOrUpdateEntry(
                        email,
                        date,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{date}")
    public ResponseEntity<MilkEntryResponse> getEntryForDate(
            Authentication authentication,
            @PathVariable LocalDate date
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                milkEntryService.getEntryForDate(
                        email,
                        date
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<MilkEntryResponse>> getEntriesForMonth(
            Authentication authentication,
            @RequestParam int year,
            @RequestParam int month
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                milkEntryService.getEntriesForMonth(
                        email,
                        year,
                        month
                )
        );
    }

    @DeleteMapping("/{date}")
    public ResponseEntity<Void> deleteEntry(
            Authentication authentication,
            @PathVariable LocalDate date
    ) {

        String email = authentication.getName();

        milkEntryService.deleteEntry(
                email,
                date
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<MonthlyMilkSummaryResponse> getMonthlySummary(
            Authentication authentication,
            @RequestParam int year,
            @RequestParam int month
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                milkEntryService.getMonthlySummary(
                        email,
                        year,
                        month
                )
        );
    }
}