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
        // Arrange
        // Configurar datos de prueba
        User user = new User();
        user.setUsername("editorUser");
        Accounting accounting = new Accounting();
        accounting.setId(1L);
        Operations operation = new Operations();
        operation.setUser(user);
        operation.setAccounting(accounting);

        Roles roles = new Roles(user, accounting, Role.EDITOR);
        // Mock RolesService para devolver el rol del editor
        when(rolesService.findByUserUsernameAndAccountingId(user.getUsername(), accounting.getId())).thenReturn(roles);
        // Mock OperationsRepository para devolver la operación guardada
        when(operationsRepository.save(operation)).thenReturn(operation);

        // Act
        // Llamar al método bajo prueba
        Operations createdOperation = operationsService.createOperation(operation);

        // Assert
        // Verificar que la operación creada no sea nula y que se haya llamado al método save del repository
        assertNotNull(createdOperation);
        verify(operationsRepository).save(operation);
    }

    @Test
    void whenCreateOperationWithoutEditorRole_thenThrowRoleException() {
        // Arrange
        // Configurar datos de prueba
        User userCreator = new User();
        userCreator.setUsername("creator");
        User user = new User();
        user.setUsername("visualizer");
        Accounting accounting = new Accounting(1L, "Accounting", null, userCreator, Type.SHARED);
        Operations operation = new Operations(1L, accounting, user, null, 100.00, "Category", LocalDate.now(), OperationType.SPENT);

        Roles roles = new Roles(user, accounting, Role.VISUALIZER);
        // Mock RolesService para devolver el rol del visualizador
        when(rolesService.findByUserUsernameAndAccountingId(user.getUsername(), accounting.getId())).thenReturn(roles);

        // Act & Assert
        // Verificar que se lanza RoleException al intentar crear la operación sin permisos de editor
        assertThrows(RoleException.class, () -> operationsService.createOperation(operation));
        // Verificar que no se llama al método save del repository
        verify(operationsRepository, never()).save(any(Operations.class));
    }

    @Test
    void whenFindAllOperationsByUserAndAccounting_thenReturnsOperations() {
        // Arrange
        // Configurar datos de prueba
        User user = new User();
        user.setUsername("user1");
        Accounting accounting = new Accounting();
        accounting.setId(1L);
        Operations operation = new Operations(null, accounting, user, null, 50.0, "Category", LocalDate.now(), OperationType.SPENT);
        List<Operations> expectedOperations = Collections.singletonList(operation);

        // Mocking setup
        // Mock RolesService para devolver los roles del usuario
        when(rolesService.findAllByUser(any(User.class))).thenReturn(Collections.singletonList(new Roles(user, accounting, Role.EDITOR)));
        // Mock OperationsRepository para devolver las operaciones esperadas
        when(operationsRepository.findByAccounting(any(Accounting.class))).thenReturn(expectedOperations);

        // Act
        // Llamar al método bajo prueba
        List<Operations> actualOperations = operationsService.findAllUserOperationByAccounting(user, accounting);

        // Assert
        // Verificar que la lista de operaciones no sea nula, no esté vacía y tenga el tamaño esperado
        assertNotNull(actualOperations);
        assertFalse(actualOperations.isEmpty(), "The operations list should not be empty");
        assertEquals(expectedOperations.size(), actualOperations.size());
        verify(operationsRepository).findByAccounting(accounting);
    }

    @Test
    void whenFindOperationsForDateRange_thenFilteredByDates() {
        // Arrange
        // Configurar datos de prueba
        User user = new User();
        user.setUsername("user1");
        Accounting accounting = new Accounting();
        accounting.setId(1L);
        LocalDate startDate = LocalDate.of(2021, 1, 1);
        LocalDate endDate = LocalDate.of(2021, 1, 31);
        Operations operation1 = new Operations(null, accounting, user, null, 100.0, "Transport", LocalDate.of(2021, 1, 15), OperationType.SPENT);
        Operations operation2 = new Operations(null, accounting, user, null, 200.0, "Food", LocalDate.of(2021, 2, 1), OperationType.SPENT);
        List<Operations> operations = Arrays.asList(operation1, operation2);

        // Mocking setup
        // Mock OperationsRepository para devolver las operaciones según la contabilidad
        when(operationsRepository.findByAccounting(accounting)).thenReturn(operations);

        // Act
        // Llamar al método bajo prueba
        List<Operations> filteredOperations = operationsService.findOperationsForDateRange(accounting, null, null, startDate, endDate);

        // Assert
        // Verificar que las operaciones filtradas contengan la operación1 y no contengan la operación2
        assertNotNull(filteredOperations);
        assertTrue(filteredOperations.contains(operation1));
        assertFalse(filteredOperations.contains(operation2));
    }

    @Test
    void whenUpdateExistingOperation_thenUpdated() {
        // Arrange
        // Configurar datos de prueba
        Long operationId = 1L;
        Operations existingOperation = new Operations(operationId, null, null, null, 50.0, "Category", LocalDate.now(), OperationType.SPENT);
        Operations updatedOperation = new Operations(operationId, null, null, null, 100.0, "Updated Category", LocalDate.now(), OperationType.SPENT);

        // Mocking setup
        // Mock OperationsRepository para verificar la existencia y obtener la operación existente
        when(operationsRepository.existsById(operationId)).thenReturn(true);
        when(operationsRepository.findById(operationId)).thenReturn(Optional.of(existingOperation));
        // Mock OperationsRepository para devolver la operación actualizada
        when(operationsRepository.save(existingOperation)).thenReturn(updatedOperation);

        // Act
        // Llamar al método bajo prueba
        Operations resultOperation = operationsService.updateOperation(operationId, updatedOperation);

        // Assert
        // Verificar que la operación resultante no sea nula y que los campos se hayan actualizado correctamente
        assertNotNull(resultOperation);
        assertEquals(updatedOperation.getQuantity(), resultOperation.getQuantity());
        assertEquals(updatedOperation.getCategory(), resultOperation.getCategory());
        verify(operationsRepository).save(existingOperation);
    }

    @Test
    void whenDeleteExistingOperation_thenDeleted() {
        // Arrange
        // Configurar datos de prueba
        Long operationId = 1L;

        // Mocking setup
        // Mock OperationsRepository para verificar la existencia y eliminar la operación
        when(operationsRepository.existsById(operationId)).thenReturn(true);

        // Act
        // Llamar al método bajo prueba
        operationsService.deleteOperation(operationId);

        // Assert
        // Verificar que se llamó al método deleteById del repository con el ID correcto
        verify(operationsRepository).deleteById(operationId);
    }

    @Test
    void whenDeleteByAccounting_thenAllOperationsDeleted() {
        // Arrange
        // Configurar datos de prueba
        Accounting accounting = new Accounting();
        accounting.setId(1L);

        // Act
        // Llamar al método bajo prueba
        operationsService.deleteByAccounting(accounting);

        // Assert
        // Verificar que se llamó al método deleteByAccounting del repository con la contabilidad correcta
        verify(operationsRepository).deleteByAccounting(accounting);
    }
}
