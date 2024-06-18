package com.econoMe.gestorgastosback.exception;

/**
 * Excepción personalizada para errores relacionados con la contabilidad.
 */
public class AccountingException extends RuntimeException {

    /**
     * Constructor que acepta un mensaje de error.
     * @param message Mensaje descriptivo del error.
     */
    public AccountingException(String message) {
        super(message);
    }

    /**
     * Constructor que acepta un mensaje de error y una excepción subyacente.
     * @param message Mensaje descriptivo del error.
     * @param e Excepción subyacente que causó esta excepción.
     */
    public AccountingException(String message, Exception e) {
        super(message, e);
    }
}
