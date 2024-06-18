package com.econoMe.gestorgastosback;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.econoMe.gestorgastosback.dto.AuthResponse;
import com.econoMe.gestorgastosback.dto.LoginDto;
import com.econoMe.gestorgastosback.repository.UserRepository;
import com.econoMe.gestorgastosback.service.AuthService;
import com.econoMe.gestorgastosback.service.JwtService;
import com.econoMe.gestorgastosback.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_Success() {
        // Arrange
        LoginDto request = new LoginDto("user", "password");
        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setPassword("securepassword");

        // Configuración del repositorio para devolver el usuario deseado
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        // Simulación del servicio JWT para devolver un token ficticio
        when(jwtService.getToken(any(UserDetails.class))).thenReturn("token");

        // Simulación del administrador de autenticación para que no lance excepciones
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(null);

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("user", response.getUsername());
        assertEquals("token", response.getToken());

        // Verificaciones para confirmar interacciones con los mocks
        verify(userRepository).findByUsername("user");
        verify(jwtService).getToken(any(UserDetails.class));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_UserNotFound() {
        // Arrange
        LoginDto request = new LoginDto("nonexistent", "password");
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> authService.login(request));

        // Verificación para confirmar que no se intentó autenticar
        verify(authenticationManager, never()).authenticate(any(Authentication.class));
    }

    @Test
    void login_InvalidPassword() {
        // Arrange
        LoginDto request = new LoginDto("user", "wrongPassword");
        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setPassword("securePassword");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        // Simulación de una excepción al autenticar debido a una contraseña incorrecta
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Contraseña incorrecta"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

}
