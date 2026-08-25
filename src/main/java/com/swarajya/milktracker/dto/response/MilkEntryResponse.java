package com.swarajya.milktracker.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class MilkEntryResponse {

    private UUID uuid;
    private LocalDate date;
    private BigDecimal morningQty;
    private BigDecimal eveningQty;
    private BigDecimal totalQty;
    private BigDecimal pricePerLiter;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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

    public BigDecimal getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(BigDecimal totalQty) {
        this.totalQty = totalQty;
    }

    public BigDecimal getPricePerLiter() {
        return pricePerLiter;
    }

    public void setPricePerLiter(BigDecimal pricePerLiter) {
        this.pricePerLiter = pricePerLiter;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}