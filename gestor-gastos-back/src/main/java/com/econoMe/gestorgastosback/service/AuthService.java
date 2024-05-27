package com.econoMe.gestorgastosback.service;

import com.econoMe.gestorgastosback.dto.AuthResponse;
import com.econoMe.gestorgastosback.dto.LoginDto;
import com.econoMe.gestorgastosback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginDto request){
        UserDetails user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        String token = jwtService.getToken(user);

        return AuthResponse.builder()
                .token(token)
                .username(request.getUsername())
                .build();
    }

}
