package com.swarajya.milktracker.exception;

public class MilkEntryNotFoundException extends RuntimeException {

    public MilkEntryNotFoundException(String message) {
        super(message);
    }
}