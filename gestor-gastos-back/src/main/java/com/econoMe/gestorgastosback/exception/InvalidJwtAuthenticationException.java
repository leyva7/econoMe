package com.econoMe.gestorgastosback.exception;

public class InvalidJwtAuthenticationException extends RuntimeException {
    public InvalidJwtAuthenticationException(String message) {
        super(message);
    }
}
