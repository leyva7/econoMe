package com.econoMe.gestorgastosback.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja la excepción BadCredentialsException que se lanza cuando las credenciales de usuario son incorrectas.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Usuario o contraseña incorrectos");
    }

    /**
     * Maneja la InvalidJwtAuthenticationException, una excepción personalizada para autenticación JWT inválida.
     */
    @ExceptionHandler(value = {InvalidJwtAuthenticationException.class})
    public ResponseEntity<Object> handleInvalidJwtAuthentication(InvalidJwtAuthenticationException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "Acceso denegado: " + ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Maneja la ExpiredJwtException que se lanza cuando un token JWT ha expirado.
     */
    @ExceptionHandler(value = {ExpiredJwtException.class})
    public ResponseEntity<Object> handleExpiredJwtException(ExpiredJwtException ex) {
        return new ResponseEntity<>("El token JWT ha expirado. Por favor, inicia sesión nuevamente.", HttpStatus.UNAUTHORIZED);
    }

    /**
     * Maneja la AccountingException, una excepción personalizada para errores relacionados con la contabilidad.
     */
    @ExceptionHandler(AccountingException.class)
    public ResponseEntity<Object> handleAccountingException(AccountingException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja la RoleException, una excepción personalizada para errores relacionados con roles.
     */
    @ExceptionHandler(RoleException.class)
    public ResponseEntity<Object> handleAccountingException(RoleException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja la OperationException, una excepción personalizada para errores relacionados con operaciones.
     */
    @ExceptionHandler(OperationException.class)
    public ResponseEntity<Object> handleOperationException(OperationException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja la UserException, una excepción personalizada para errores relacionados con usuarios.
     */
    @ExceptionHandler(UserException.class)
    public ResponseEntity<Object> handleUserException(UserException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja todas las demás excepciones genéricas que no tienen un manejador específico.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

}
