package com.swarajya.milktracker.dto.response;

import java.math.BigDecimal;

public class MonthlyMilkSummaryResponse {

    private int year;
    private int month;

    private int totalDays;

    private BigDecimal totalMorningQty;
    private BigDecimal totalEveningQty;
    private BigDecimal totalQty;
    private BigDecimal totalAmount;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }

    public BigDecimal getTotalMorningQty() {
        return totalMorningQty;
    }

    public void setTotalMorningQty(BigDecimal totalMorningQty) {
        this.totalMorningQty = totalMorningQty;
    }

    public BigDecimal getTotalEveningQty() {
        return totalEveningQty;
    }

    public void setTotalEveningQty(BigDecimal totalEveningQty) {
        this.totalEveningQty = totalEveningQty;
    }

    public BigDecimal getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(BigDecimal totalQty) {
        this.totalQty = totalQty;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}