package com.darwin.authservice.infrastructure.exceptionhandler;

import lombok.Getter;

@Getter
public enum ExceptionMessage {
    INVALID_CREDENTIALS("Invalid credentials");

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }
}
