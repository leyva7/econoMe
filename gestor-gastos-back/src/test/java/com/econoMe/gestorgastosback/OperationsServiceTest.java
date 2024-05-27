package com.econoMe.gestorgastosback;

import com.econoMe.gestorgastosback.common.OperationType;
import com.econoMe.gestorgastosback.common.Role;
import com.econoMe.gestorgastosback.common.Type;
import com.econoMe.gestorgastosback.exception.RoleException;
import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.Operations;
import com.econoMe.gestorgastosback.model.Roles;
import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.repository.OperationsRepository;
import com.econoMe.gestorgastosback.service.OperationsService;
import com.econoMe.gestorgastosback.service.RolesService;
import jakarta.persistence.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OperationsServiceTest {

    @Mock
    private OperationsRepository operationsRepository;

    @Mock
    private RolesService rolesService;

    @InjectMocks
    private OperationsService operationsService;

    @Test
    void whenCreateOperationWithEditorRole_thenOperationIsSaved() {
        User user = new User();
        user.setUsername("editorUser");
        Accounting accounting = new Accounting();
        accounting.setId(1L);
        Operations operation = new Operations();
        operation.setUser(user);
        operation.setAccounting(accounting);

        Roles roles = new Roles(user, accounting, Role.EDITOR);
        when(rolesService.findByUserUsernameAndAccountingId(user.getUsername(), accounting.getId())).thenReturn(roles);
        when(operationsRepository.save(operation)).thenReturn(operation);

        Operations createdOperation = operationsService.createOperation(operation);

        assertNotNull(createdOperation);
        verify(operationsRepository).save(operation);
    }

    @Test
    void whenCreateOperationWithoutEditorRole_thenThrowRoleException() {
        User userCreator = new User();
        userCreator.setUsername("creator");
        User user = new User();
        user.setUsername("visualizer");
        Accounting accounting = new Accounting(1L, "Accounting", null, userCreator, Type.SHARED);
        Operations operation = new Operations(1L, accounting, user, null, 100.00, "Category", LocalDate.now(), OperationType.SPENT);

        Roles roles = new Roles(user, accounting, Role.VISUALIZER);
        when(rolesService.findByUserUsernameAndAccountingId(user.getUsername(), accounting.getId())).thenReturn(roles);

        assertThrows(RoleException.class, () -> operationsService.createOperation(operation));
        verify(operationsRepository, never()).save(any(Operations.class));
    }

    @Test
    void whenFindAllOperationsByUserAndAccounting_thenReturnsOperations() {
        // Preparar
        User user = new User();
        user.setUsername("user1");
        Accounting accounting = new Accounting();
        accounting.setId(1L);
        Operations operation = new Operations(null, accounting, user, null, 50.0, "Category", LocalDate.now(), OperationType.SPENT);
        List<Operations> expectedOperations = Collections.singletonList(operation);

        // Configuración de los mocks
        when(rolesService.findAllByUser(any(User.class))).thenReturn(Collections.singletonList(new Roles(user, accounting, Role.EDITOR)));
        when(operationsRepository.findByAccounting(any(Accounting.class))).thenReturn(expectedOperations);

        // Actuar
        List<Operations> actualOperations = operationsService.findAllUserOperationByAccounting(user, accounting);

        // Verificar
        assertNotNull(actualOperations);
        assertFalse(actualOperations.isEmpty(), "The operations list should not be empty");
        assertEquals(expectedOperations.size(), actualOperations.size());
        verify(operationsRepository).findByAccounting(accounting);
    }

    @Test
    void whenFindOperationsForDateRange_thenFilteredByDates() {
        // Preparar
        User user = new User();
        user.setUsername("user1");
        Accounting accounting = new Accounting();
        accounting.setId(1L);
        LocalDate startDate = LocalDate.of(2021, 1, 1);
        LocalDate endDate = LocalDate.of(2021, 1, 31);
        Operations operation1 = new Operations(null, accounting, user, null, 100.0, "Transport", LocalDate.of(2021, 1, 15), OperationType.SPENT);
        Operations operation2 = new Operations(null, accounting, user, null, 200.0, "Food", LocalDate.of(2021, 2, 1), OperationType.SPENT);
        List<Operations> operations = Arrays.asList(operation1, operation2);

        // Configuración de los mocks
        when(operationsRepository.findByAccounting(accounting)).thenReturn(operations);

        // Actuar
        List<Operations> filteredOperations = operationsService.findOperationsForDateRange(accounting, null, null, startDate, endDate);

        // Verificar
        assertNotNull(filteredOperations);
        assertTrue(filteredOperations.contains(operation1));
        assertFalse(filteredOperations.contains(operation2));
    }

    @Test
    void whenUpdateExistingOperation_thenUpdated() {
        // Preparar
        Long operationId = 1L;
        Operations existingOperation = new Operations(operationId, null, null, null, 50.0, "Category", LocalDate.now(), OperationType.SPENT);
        Operations updatedOperation = new Operations(operationId, null, null, null, 100.0, "Updated Category", LocalDate.now(), OperationType.SPENT);

        // Configuración de los mocks
        when(operationsRepository.existsById(operationId)).thenReturn(true);
        when(operationsRepository.findById(operationId)).thenReturn(Optional.of(existingOperation));
        when(operationsRepository.save(existingOperation)).thenReturn(updatedOperation);

        // Actuar
        Operations resultOperation = operationsService.updateOperation(operationId, updatedOperation);

        // Verificar
        assertNotNull(resultOperation);
        assertEquals(updatedOperation.getQuantity(), resultOperation.getQuantity());
        assertEquals(updatedOperation.getCategory(), resultOperation.getCategory());
        verify(operationsRepository).save(existingOperation);
    }

    @Test
    void whenDeleteExistingOperation_thenDeleted() {
        // Preparar
        Long operationId = 1L;

        // Configuración de los mocks
        when(operationsRepository.existsById(operationId)).thenReturn(true);

        // Actuar
        operationsService.deleteOperation(operationId);

        // Verificar
        verify(operationsRepository).deleteById(operationId);
    }

    @Test
    void whenDeleteByAccounting_thenAllOperationsDeleted() {
        // Preparar
        Accounting accounting = new Accounting();
        accounting.setId(1L);

        // Actuar
        operationsService.deleteByAccounting(accounting);

        // Verificar
        verify(operationsRepository).deleteByAccounting(accounting);
    }


}
