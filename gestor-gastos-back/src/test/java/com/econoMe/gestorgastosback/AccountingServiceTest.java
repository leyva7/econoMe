package com.econoMe.gestorgastosback;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.econoMe.gestorgastosback.common.Type;
import com.econoMe.gestorgastosback.exception.AccountingException;
import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.Roles;
import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.repository.AccountingRepository;
import com.econoMe.gestorgastosback.service.AccountingService;
import com.econoMe.gestorgastosback.service.OperationsService;
import com.econoMe.gestorgastosback.service.RolesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AccountingServiceTest {

    @Mock
    private AccountingRepository accountingRepository;
    @Mock
    private OperationsService operationsService;
    @Mock
    private RolesService rolesService;

    @InjectMocks
    private AccountingService accountingService;

    private User user;
    private Accounting accounting;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("user");

        accounting = new Accounting();
        accounting.setId(1L);
        accounting.setName("Test Accounting");
        accounting.setDescription("Description");
        accounting.setUserCreator(user);
        accounting.setType(Type.PERSONAL);
    }

    @Test
    void createAccounting_Success() {
        when(accountingRepository.save(any(Accounting.class))).thenReturn(accounting);
        when(rolesService.createRole(any(Roles.class))).thenReturn(new Roles());

        Accounting result = accountingService.createAccounting(accounting);
        assertNotNull(result);
        assertEquals(accounting.getName(), result.getName());

        verify(accountingRepository).save(accounting);
        verify(rolesService).createRole(any(Roles.class));
    }

    @Test
    void createAccounting_DataAccessException() {
        // Simula la excepción de acceso a datos al intentar guardar la contabilidad
        when(accountingRepository.save(any(Accounting.class)))
                .thenThrow(new DataIntegrityViolationException("DB error"));

        // Verifica que se lanza la excepción correcta al intentar crear la contabilidad
        assertThrows(DataIntegrityViolationException.class, () -> accountingService.createAccounting(new Accounting()));

        // Verifica que no se intenta crear un rol si falla la creación de la contabilidad
        verify(rolesService, never()).createRole(any(Roles.class));
    }


    @Test
    void findAccountingById_NotFound_ThrowsException() {
        when(accountingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AccountingException.class, () -> accountingService.findAccountingById(1L));
    }

    @Test
    void updateAccounting_Success() {
        when(accountingRepository.existsById(accounting.getId())).thenReturn(true);
        when(accountingRepository.findById(accounting.getId())).thenReturn(Optional.of(accounting));
        when(accountingRepository.save(accounting)).thenReturn(accounting);

        Accounting updated = accountingService.updateAccounting(accounting.getId(), user, "New Name", "New Description");
        assertNotNull(updated);
        assertEquals("New Name", updated.getName());
        assertEquals("New Description", updated.getDescription());

        verify(accountingRepository).save(accounting);
    }

    @Test
    void deleteAccounting_NotCreator_ThrowsException() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("anotherUser");

        when(accountingRepository.findById(accounting.getId())).thenReturn(Optional.of(accounting));

        assertThrows(AccountingException.class, () -> accountingService.deleteAccounting(accounting.getId(), anotherUser));
    }

    @Test
    void deleteAccounting_Success() {
        when(accountingRepository.findById(accounting.getId())).thenReturn(Optional.of(accounting));
        doNothing().when(operationsService).deleteByAccounting(accounting);
        doNothing().when(rolesService).deleteByAccounting(accounting);
        doNothing().when(accountingRepository).deleteById(accounting.getId());

        assertDoesNotThrow(() -> accountingService.deleteAccounting(accounting.getId(), user));

        verify(operationsService).deleteByAccounting(accounting);
        verify(rolesService).deleteByAccounting(accounting);
        verify(accountingRepository).deleteById(accounting.getId());
    }
}