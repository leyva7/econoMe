package com.econoMe.gestorgastosback.exception;

// Definición de la clase OperationException que extiende RuntimeException
public class OperationException extends RuntimeException {

    // Constructor que acepta un mensaje de error
    public OperationException(String message) {
        // Llama al constructor de la clase base (RuntimeException) con el mensaje proporcionado
        super(message);
    }

    // Constructor que acepta un mensaje de error y una excepción causa
    public OperationException(String message, Exception e) {
        // Llama al constructor de la clase base (RuntimeException) con el mensaje y la causa proporcionados
        super(message, e);
    }
}
