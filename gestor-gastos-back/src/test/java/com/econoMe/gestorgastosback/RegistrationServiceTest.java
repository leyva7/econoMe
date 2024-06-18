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
        // Inicializar los mocks antes de cada test
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRegisterUserAndAccounting() {
        // Arrange
        // Configurar datos de prueba
        RegistrationDto registrationDto = new RegistrationDto();
        User newUser = new User();
        User savedUser = new User();
        Accounting newAccounting = new Accounting();
        UserDto userDto = new UserDto();

        registrationDto.setUser(newUser);
        // Mock UserService para devolver el usuario guardado
        when(userService.createUser(newUser)).thenReturn(savedUser);
        // Mock MappingService para convertir un AccountingDto a Accounting
        when(mappingService.accountingDtoToAccounting(any())).thenReturn(newAccounting);
        // Mock MappingService para convertir User a UserDto
        when(mappingService.userToDto(savedUser)).thenReturn(userDto);

        // Act
        // Llamar al método bajo prueba
        UserDto result = registrationService.registerUserAndAccounting(registrationDto);

        // Assert
        // Verificar resultados esperados
        assertEquals(userDto, result);
        verify(userService, times(1)).createUser(newUser);
        verify(accountingService, times(1)).createFirstAccounting(newAccounting);
        verify(mappingService, times(1)).accountingDtoToAccounting(any());
        verify(mappingService, times(1)).userToDto(savedUser);
    }

    @Test
    public void testRegisterUserWithExistingUsername() {
        // Arrange
        // Configurar datos de prueba
        User existingUser = new User();
        existingUser.setUsername("existingUsername");
        RegistrationDto registrationDto = new RegistrationDto(existingUser, new AccountingDto());

        // Mock UserService para simular un usuario existente con el mismo username
        Mockito.when(userService.createUser(existingUser)).thenThrow(new UserException("El usuario ya existe"));

        // Act & Assert
        // Verificar que se lanza la excepción esperada
        assertThrows(UserException.class, () -> registrationService.registerUserAndAccounting(registrationDto));

        // Verify
        // Verificar interacciones con los mocks
        verify(userService, times(1)).createUser(existingUser);
        verifyNoInteractions(accountingService);
        verifyNoInteractions(mappingService);
    }

    @Test
    public void testRegisterUserWithExistingEmail() {
        // Arrange
        // Configurar datos de prueba
        User existingUser = new User();
        existingUser.setMail("existing@example.com");
        RegistrationDto registrationDto = new RegistrationDto(existingUser, new AccountingDto());

        // Mock UserService para simular un usuario existente con el mismo email
        Mockito.when(userService.createUser(existingUser)).thenThrow(new UserException("El correo electrónico ya está en uso"));

        // Act & Assert
        // Verificar que se lanza la excepción esperada
        assertThrows(UserException.class, () -> registrationService.registerUserAndAccounting(registrationDto));

        // Verify
        // Verificar interacciones con los mocks
        verify(userService, times(1)).createUser(existingUser);
        verifyNoInteractions(accountingService);
        verifyNoInteractions(mappingService);
    }
}
