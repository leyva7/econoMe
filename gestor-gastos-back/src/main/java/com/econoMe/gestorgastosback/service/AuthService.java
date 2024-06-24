package com.econoMe.gestorgastosback.service;

import com.econoMe.gestorgastosback.dto.AuthResponse;
import com.econoMe.gestorgastosback.dto.LoginDto;
import com.econoMe.gestorgastosback.repository.UserRepository;
import com.econoMe.gestorgastosback.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService; // Servicio para generar tokens JWT
    private final UserRepository userRepository; // Repositorio de usuarios para buscar por nombre de usuario
    private final AuthenticationManager authenticationManager; // Gestor de autenticación de Spring Security
    private final UserService userService;

    // Método para realizar el login de un usuario
    public AuthResponse login(LoginDto request) {
        // Busca al usuario por su nombre de usuario
        UserDetails user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        User user1 = userService.getUserByUsername(request.getUsername());

        // Autentica al usuario con Spring Security
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        // Genera el token JWT para el usuario autenticado
        String token = jwtService.getToken(user, user1.getId());

        // Devuelve la respuesta de autenticación con el token y el nombre de usuario
        return AuthResponse.builder()
                .token(token)
                .username(request.getUsername())
                .build();
    }
}
