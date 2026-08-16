package com.swarajya.milktracker.exception;

public class DuplicateEmailException extends  RuntimeException{
    public DuplicateEmailException(String message) {
        super(message);
    }
}
