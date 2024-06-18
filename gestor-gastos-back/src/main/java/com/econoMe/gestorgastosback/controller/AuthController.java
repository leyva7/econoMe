package com.econoMe.gestorgastosback.controller;

import com.econoMe.gestorgastosback.dto.LoginDto;
import com.econoMe.gestorgastosback.dto.RegistrationDto;
import com.econoMe.gestorgastosback.dto.UserDto;
import com.econoMe.gestorgastosback.service.AuthService;
import com.econoMe.gestorgastosback.service.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final RegistrationService registrationService;

    // Constructor para inyectar las dependencias necesarias
    AuthController(PasswordEncoder passwordEncoder, AuthService authService, RegistrationService registrationService) {
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
        this.registrationService = registrationService;
    }

    // Endpoint para el registro de usuario
    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody RegistrationDto registrationDto) {
        // Verificar que la contraseña no esté vacía
        if (registrationDto.getUser().getPassword() == null || registrationDto.getUser().getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }

        // Codificar la contraseña antes de guardar el usuario
        registrationDto.getUser().setPassword(passwordEncoder.encode(registrationDto.getUser().getPassword()));

        // Registrar el usuario y la contabilidad asociada
        UserDto savedUser = registrationService.registerUserAndAccounting(registrationDto);
        return ResponseEntity.ok(savedUser);
    }

    // Endpoint para el inicio de sesión del usuario
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(authService.login(loginDto));
    }
}
