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
        // Configuración inicial antes de cada prueba
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
        // Prueba para verificar la creación exitosa de una contabilidad

        // Configuración del mock para simular el comportamiento del repositorio
        when(accountingRepository.save(any(Accounting.class))).thenReturn(accounting);
        when(rolesService.createRole(any(Roles.class))).thenReturn(new Roles());

        // Ejecución del método a probar
        Accounting result = accountingService.createAccounting(accounting);

        // Verificación de los resultados esperados
        assertNotNull(result);
        assertEquals(accounting.getName(), result.getName());

        // Verificación de interacciones con los mocks
        verify(accountingRepository).save(accounting);
        verify(rolesService).createRole(any(Roles.class));
    }

    @Test
    void createAccounting_DataAccessException() {
        // Prueba para verificar el manejo de excepciones al crear una contabilidad

        // Configuración del mock para simular una excepción al guardar en el repositorio
        when(accountingRepository.save(any(Accounting.class)))
                .thenThrow(new DataIntegrityViolationException("DB error"));

        // Verificación de que se lanza la excepción adecuada
        assertThrows(DataIntegrityViolationException.class, () -> accountingService.createAccounting(new Accounting()));

        // Verificación de que no se intenta crear un rol si falla la creación de la contabilidad
        verify(rolesService, never()).createRole(any(Roles.class));
    }

    @Test
    void findAccountingById_NotFound_ThrowsException() {
        // Prueba para verificar el manejo de excepciones cuando no se encuentra una contabilidad por ID

        // Configuración del mock para simular que no se encuentra la contabilidad
        when(accountingRepository.findById(1L)).thenReturn(Optional.empty());

        // Verificación de que se lanza la excepción adecuada
        assertThrows(AccountingException.class, () -> accountingService.findAccountingById(1L));
    }

    @Test
    void updateAccounting_Success() {
        // Prueba para verificar la actualización exitosa de una contabilidad

        // Configuración del mock para simular el comportamiento del repositorio
        when(accountingRepository.existsById(accounting.getId())).thenReturn(true);
        when(accountingRepository.findById(accounting.getId())).thenReturn(Optional.of(accounting));
        when(accountingRepository.save(accounting)).thenReturn(accounting);

        // Ejecución del método a probar
        Accounting updated = accountingService.updateAccounting(accounting.getId(), user, "New Name", "New Description");

        // Verificación de los resultados esperados
        assertNotNull(updated);
        assertEquals("New Name", updated.getName());
        assertEquals("New Description", updated.getDescription());

        // Verificación de interacciones con los mocks
        verify(accountingRepository).save(accounting);
    }

    @Test
    void deleteAccounting_NotCreator_ThrowsException() {
        // Prueba para verificar el manejo de excepciones al intentar eliminar una contabilidad por un usuario que no es el creador

        // Configuración del mock para simular la existencia de la contabilidad
        when(accountingRepository.findById(accounting.getId())).thenReturn(Optional.of(accounting));

        // Verificación de que se lanza la excepción adecuada
        assertThrows(AccountingException.class, () -> accountingService.deleteAccounting(accounting.getId(), new User()));
    }

    @Test
    void deleteAccounting_Success() {
        // Prueba para verificar la eliminación exitosa de una contabilidad

        // Configuración del mock para simular el comportamiento del repositorio y servicios relacionados
        when(accountingRepository.findById(accounting.getId())).thenReturn(Optional.of(accounting));
        doNothing().when(operationsService).deleteByAccounting(accounting);
        doNothing().when(rolesService).deleteByAccounting(accounting);
        doNothing().when(accountingRepository).deleteById(accounting.getId());

        // Ejecución del método a probar
        assertDoesNotThrow(() -> accountingService.deleteAccounting(accounting.getId(), user));

        // Verificación de interacciones con los mocks
        verify(operationsService).deleteByAccounting(accounting);
        verify(rolesService).deleteByAccounting(accounting);
        verify(accountingRepository).deleteById(accounting.getId());
    }
}
