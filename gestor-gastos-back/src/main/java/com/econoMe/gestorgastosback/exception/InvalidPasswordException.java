package com.econoMe.gestorgastosback.exception;

// Definición de la clase InvalidPasswordException que extiende RuntimeException
public class InvalidPasswordException extends RuntimeException {

    // Constructor que acepta un mensaje de error
    public InvalidPasswordException(String message) {
        // Llama al constructor de la clase base (RuntimeException) con el mensaje proporcionado
        super(message);
    }
}
