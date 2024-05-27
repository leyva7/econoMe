package com.econoMe.gestorgastosback;

import com.econoMe.gestorgastosback.dto.AccountingDto;
import com.econoMe.gestorgastosback.dto.RegistrationDto;
import com.econoMe.gestorgastosback.dto.UserDto;
import com.econoMe.gestorgastosback.exception.UserException;
import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.service.AccountingService;
import com.econoMe.gestorgastosback.service.MappingService;
import com.econoMe.gestorgastosback.service.RegistrationService;
import com.econoMe.gestorgastosback.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.net.UnknownServiceException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RegistrationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private AccountingService accountingService;

    @Mock
    private MappingService mappingService;

    @InjectMocks
    private RegistrationService registrationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRegisterUserAndAccounting() {
        // Arrange
        RegistrationDto registrationDto = new RegistrationDto();
        User newUser = new User();
        User savedUser = new User();
        Accounting newAccounting = new Accounting();
        UserDto userDto = new UserDto();

        registrationDto.setUser(newUser);
        when(userService.createUser(newUser)).thenReturn(savedUser);
        when(mappingService.accountingDtoToAccounting(any())).thenReturn(newAccounting);
        when(mappingService.userToDto(savedUser)).thenReturn(userDto);

        // Act
        UserDto result = registrationService.registerUserAndAccounting(registrationDto);

        // Assert
        assertEquals(userDto, result);
        verify(userService, times(1)).createUser(newUser);
        verify(accountingService, times(1)).createFirstAccounting(newAccounting);
        verify(mappingService, times(1)).accountingDtoToAccounting(any());
        verify(mappingService, times(1)).userToDto(savedUser);
    }

    @Test
    public void testRegisterUserWithExistingUsername() {
        // Configuración del usuario y el DTO de registro
        User existingUser = new User();
        existingUser.setUsername("existingUsername");
        RegistrationDto registrationDto = new RegistrationDto(existingUser, new AccountingDto());

        // Mock del UserService para simular que ya existe un usuario con el mismo username
        Mockito.when(userService.createUser(existingUser)).thenThrow(new UserException("El usuario ya existe"));

        // Verificar que se lanza la excepción esperada al intentar registrar un usuario con el mismo username
        assertThrows(UserException.class, () -> registrationService.registerUserAndAccounting(registrationDto));
    }

    @Test
    public void testRegisterUserWithExistingEmail() {
        // Configuración del usuario y el DTO de registro
        User existingUser = new User();
        existingUser.setMail("existing@example.com");
        RegistrationDto registrationDto = new RegistrationDto(existingUser, new AccountingDto());

        // Mock del UserService para simular que ya existe un usuario con el mismo correo electrónico
        Mockito.when(userService.createUser(existingUser)).thenThrow(new UserException("El correo electrónico ya está en uso"));

        // Verificar que se lanza la excepción esperada al intentar registrar un usuario con el mismo correo electrónico
        assertThrows(UserException.class, () -> registrationService.registerUserAndAccounting(registrationDto));
    }
}
