package com.swarajya.milktracker.exception;

public class UserSettingsNotFoundException extends RuntimeException {

    public UserSettingsNotFoundException(String message) {
        super(message);
    }
}