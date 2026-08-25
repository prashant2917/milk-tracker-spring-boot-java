package com.swarajya.milktracker.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "milk_entries",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_milk_entry_user_date",
                        columnNames = {"user_id", "entry_date"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_milk_entry_user_date",
                        columnList = "user_id, entry_date"
                )
        }
)
public class MilkEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Column(
            name = "entry_date",
            nullable = false
    )
    private LocalDate date;

    @Column(
            name = "morning_qty",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal morningQty;

    @Column(
            name = "evening_qty",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal eveningQty;

    @Column(
            name = "price_per_liter",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal pricePerLiter;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getUuid() {
        return uuid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getMorningQty() {
        return morningQty;
    }

    public void setMorningQty(BigDecimal morningQty) {
        this.morningQty = morningQty;
    }

    public BigDecimal getEveningQty() {
        return eveningQty;
    }

    public void setEveningQty(BigDecimal eveningQty) {
        this.eveningQty = eveningQty;
    }

    public BigDecimal getPricePerLiter() {
        return pricePerLiter;
    }

    public void setPricePerLiter(BigDecimal pricePerLiter) {
        this.pricePerLiter = pricePerLiter;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}