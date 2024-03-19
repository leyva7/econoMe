package com.econoMe.gestorgastosback.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Usuario o contraseña incorrectos");
    }

    @ExceptionHandler(value = {InvalidJwtAuthenticationException.class})
    public ResponseEntity<Object> handleInvalidJwtAuthentication(InvalidJwtAuthenticationException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "Acceso denegado: " + ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {ExpiredJwtException.class})
    public ResponseEntity<Object> handleExpiredJwtException(ExpiredJwtException ex) {
        return new ResponseEntity<>("El token JWT ha expirado. Por favor, inicia sesión nuevamente.", HttpStatus.UNAUTHORIZED);
    }

}
