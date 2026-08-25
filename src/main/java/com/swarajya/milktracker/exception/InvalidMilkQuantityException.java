package com.swarajya.milktracker.exception;

public class InvalidMilkQuantityException extends RuntimeException {

    public InvalidMilkQuantityException(String message) {
        super(message);
    }
}