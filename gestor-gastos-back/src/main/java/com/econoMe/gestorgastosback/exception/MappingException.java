package com.econoMe.gestorgastosback.exception;

// Definición de la clase MappingException que extiende RuntimeException
public class MappingException extends RuntimeException {

    // Constructor que acepta un mensaje de error
    public MappingException(String message) {
        // Llama al constructor de la clase base (RuntimeException) con el mensaje proporcionado
        super(message);
    }

    // Constructor que acepta un mensaje de error y una excepción causa
    public MappingException(String message, Exception e) {
        // Llama al constructor de la clase base (RuntimeException) con el mensaje y la causa proporcionados
        super(message, e);
    }
}
