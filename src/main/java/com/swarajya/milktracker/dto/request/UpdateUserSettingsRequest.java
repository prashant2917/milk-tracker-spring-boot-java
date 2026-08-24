package com.swarajya.milktracker.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class UpdateUserSettingsRequest {

    @NotNull(message = "Default price per liter is required")
    @DecimalMin(
            value = "0.01",
            message = "Default price per liter must be greater than zero"
    )
    private BigDecimal defaultPricePerLiter;

    public BigDecimal getDefaultPricePerLiter() {
        return defaultPricePerLiter;
    }

    public void setDefaultPricePerLiter(
            BigDecimal defaultPricePerLiter
    ) {
        this.defaultPricePerLiter =
                defaultPricePerLiter;
    }
}