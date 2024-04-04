package com.econoMe.gestorgastosback.exception;

public class AccountingException extends RuntimeException {
    public AccountingException(String message) {
        super(message);
    }

    public AccountingException(String message, Exception e) {
        super(message, e);
    }
}