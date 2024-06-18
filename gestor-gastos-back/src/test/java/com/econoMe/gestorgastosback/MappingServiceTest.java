package com.econoMe.gestorgastosback;

import com.econoMe.gestorgastosback.common.Type;
import com.econoMe.gestorgastosback.dto.AccountingDto;
import com.econoMe.gestorgastosback.dto.UserDto;
import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.service.AccountingService;
import com.econoMe.gestorgastosback.service.MappingService;
import com.econoMe.gestorgastosback.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MappingServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private AccountingService accountingService;

    @InjectMocks
    private MappingService mappingService;

    @Test
    void whenMappingUserToDto_thenCorrectlyMapped() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("securepassword");
        user.setMail("testuser@example.com");

        // Simulación de comportamiento del UserService si fuera necesario
        // when(userService.someMethod()).thenReturn(...);

        // Act
        UserDto result = mappingService.userToDto(user);

        // Assert
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getSurname(), result.getSurname());
        assertEquals(user.getMail(), result.getMail());
    }

    @Test
    void whenMappingAccountingToDto_thenCorrectlyMapped() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("securepassword");
        user.setMail("testuser@example.com");
        Accounting accounting = new Accounting(1L, "Accounting Name", "Description", user, Type.SHARED);

        // Simulación de comportamiento del AccountingService si fuera necesario
        // when(accountingService.someMethod()).thenReturn(...);

        // Act
        AccountingDto result = mappingService.accountingToDto(accounting);

        // Assert
        assertEquals(accounting.getId(), result.getId());
        assertEquals(accounting.getName(), result.getName());
        assertEquals(accounting.getDescription(), result.getDescription());
        assertEquals(accounting.getType(), result.getType());
        assertEquals(accounting.getUserCreator().getName(), result.getUserCreator());
    }

    @Test
    void whenConvertingDtoToAccounting_thenCorrectlyMapped() {
        // Arrange
        AccountingDto dto = new AccountingDto(1L, "Accounting Name", "Description", "User Name", Type.SHARED);
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("securepassword");
        user.setMail("testuser@example.com");

        // Simulación de comportamiento del UserService o AccountingService si fuera necesario
        // when(userService.someMethod()).thenReturn(...);
        // when(accountingService.someMethod()).thenReturn(...);

        // Act
        Accounting result = mappingService.accountingDtoToAccounting(dto);

        // Assert
        assertNotNull(result);
        assertEquals(dto.getId(), result.getId());
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals(dto.getType(), result.getType());
    }
}
