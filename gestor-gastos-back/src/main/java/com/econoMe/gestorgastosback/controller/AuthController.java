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

    AuthController(PasswordEncoder passwordEncoder, AuthService authService, RegistrationService registrationService) {
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
        this.registrationService = registrationService;
    }

    // Registro de usuario
    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody RegistrationDto registrationDto) {
        if (registrationDto.getUser().getPassword() == null || registrationDto.getUser().getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }

        registrationDto.getUser().setPassword(passwordEncoder.encode(registrationDto.getUser().getPassword()));

        UserDto savedUser = registrationService.registerUserAndAccounting(registrationDto);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(authService.login(loginDto));
    }
}
