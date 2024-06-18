package com.econoMe.gestorgastosback.exception;

// Definición de la clase UserException que extiende RuntimeException
public class UserException extends RuntimeException {

    // Constructor que acepta un mensaje de error
    public UserException(String message) {
        // Llama al constructor de la clase base (RuntimeException) con el mensaje proporcionado
        super(message);
    }

    // Constructor que acepta un mensaje de error y una excepción causa
    public UserException(String message, Exception e) {
        // Llama al constructor de la clase base (RuntimeException) con el mensaje y la causa proporcionados
        super(message, e);
    }
}
