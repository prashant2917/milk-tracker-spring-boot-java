package com.swarajya.milktracker.dto.response;

import java.math.BigDecimal;

public class UserSettingsResponse {

    private BigDecimal defaultPricePerLiter;

    public UserSettingsResponse(
            BigDecimal defaultPricePerLiter
    ) {
        this.defaultPricePerLiter =
                defaultPricePerLiter;
    }

    public BigDecimal getDefaultPricePerLiter() {
        return defaultPricePerLiter;
    }
}