package com.econoMe.gestorgastosback.exception;

public class RoleException extends RuntimeException {
    public RoleException(String message) {
        super(message);
    }

    public RoleException(String message, Exception e) {
        super(message, e);
    }
}