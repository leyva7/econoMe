package com.econoMe.gestorgastosback.exception;

public class OperationException extends RuntimeException {
    public OperationException(String message) {
        super(message);
    }

    public OperationException(String message, Exception e) {
        super(message, e);
    }
}