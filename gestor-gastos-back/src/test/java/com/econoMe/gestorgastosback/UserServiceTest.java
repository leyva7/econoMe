package com.econoMe.gestorgastosback;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.econoMe.gestorgastosback.dto.UserDto;
import com.econoMe.gestorgastosback.exception.UserException;
import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.repository.UserRepository;
import com.econoMe.gestorgastosback.service.AccountingService;
import com.econoMe.gestorgastosback.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AccountingService accountingService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("securepassword");
        user.setMail("testuser@example.com");
    }

    @Test
    void createUser_UsernameExists_ThrowsException() {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        UserException thrown = assertThrows(UserException.class, () -> userService.createUser(user));
        assertEquals("El nombre de usuario ya está en uso", thrown.getMessage());
    }

    @Test
    void createUser_EmailExists_ThrowsException() {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.existsByMail(user.getMail())).thenReturn(true);

        UserException thrown = assertThrows(UserException.class, () -> userService.createUser(user));
        assertEquals("El correo electrónico ya está en uso", thrown.getMessage());
    }

    @Test
    void createUser_Success_ReturnsUser() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByMail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.createUser(user);
        assertNotNull(savedUser);
        assertEquals("testuser", savedUser.getUsername());
    }

    @Test
    void getAllUser_ReturnsListOfUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));

        List<User> users = userService.getAllUser();
        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
    }

    @Test
    void getUserByUsername_NotFound_ThrowsException() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        UserException thrown = assertThrows(UserException.class, () -> userService.getUserByUsername("nonexistentuser"));
        assertTrue(thrown.getMessage().contains("No se encontró el usuario con nombre de usuario"));
    }

    @Test
    void findById_NotFound_ThrowsException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserException thrown = assertThrows(UserException.class, () -> userService.findById(99L));
        assertTrue(thrown.getMessage().contains("El usuario con id 99 no ha sido encontrado."));
    }

    @Test
    void updateDetails_UserNotFound_ThrowsException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserException thrown = assertThrows(UserException.class, () -> userService.updateDetails(99L, new UserDto()));
        assertTrue(thrown.getMessage().contains("El usuario con id 99 no ha sido encontrado."));
    }

    @Test
    void updatePassword_IncorrectCurrentPassword_ThrowsException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        UserException thrown = assertThrows(UserException.class, () -> userService.updatePassword(1L, "wrongpassword", "newpassword"));
        assertEquals("La contraseña actual es incorrecta.", thrown.getMessage());
    }

    @Test
    void deleteUser_UserDoesNotExist_ThrowsException() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        UserException thrown = assertThrows(UserException.class, () -> userService.deleteUser(99L));
        assertEquals("El usuario con id 99 no existe.", thrown.getMessage());
    }
}
