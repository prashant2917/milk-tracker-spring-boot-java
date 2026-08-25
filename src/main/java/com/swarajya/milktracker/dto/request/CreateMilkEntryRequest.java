package com.swarajya.milktracker.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CreateMilkEntryRequest {

    @NotNull(message = "Morning quantity is required")
    private BigDecimal morningQty;

    @NotNull(message = "Evening quantity is required")
    private BigDecimal eveningQty;

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
}