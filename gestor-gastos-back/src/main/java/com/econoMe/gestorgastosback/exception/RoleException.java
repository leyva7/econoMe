package com.econoMe.gestorgastosback.exception;

// Definición de la clase RoleException que extiende RuntimeException
public class RoleException extends RuntimeException {

    // Constructor que acepta un mensaje de error
    public RoleException(String message) {
        // Llama al constructor de la clase base (RuntimeException) con el mensaje proporcionado
        super(message);
    }

    // Constructor que acepta un mensaje de error y una excepción causa
    public RoleException(String message, Exception e) {
        // Llama al constructor de la clase base (RuntimeException) con el mensaje y la causa proporcionados
        super(message, e);
    }
}
