package com.econoMe.gestorgastosback;

import com.econoMe.gestorgastosback.common.Role;
import com.econoMe.gestorgastosback.model.Roles;
import com.econoMe.gestorgastosback.model.RolesId;
import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.repository.RolesRepository;
import com.econoMe.gestorgastosback.service.RolesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RolesServiceTest {

    @Mock
    private RolesRepository rolesRepository;

    @InjectMocks
    private RolesService rolesService;

    @Test
    public void testCreateRole() {
        // Arrange
        // Configurar el objeto de prueba
        Roles role = new Roles();
        role.setRole(Role.EDITOR);

        // Mock del método save del repositorio para devolver el mismo rol
        when(rolesRepository.save(any(Roles.class))).thenReturn(role);

        // Act
        // Llamar al método bajo prueba
        Roles createdRole = rolesService.createRole(role);

        // Assert
        // Verificar que el rol creado no sea nulo y tenga el rol correcto
        assertNotNull(createdRole);
        assertEquals(Role.EDITOR, createdRole.getRole());
    }

    @Test
    public void testFindAllByUser() {
        // Arrange
        // Configurar el usuario de prueba
        User user = new User();
        user.setId(1L);

        // Configurar roles de prueba asociados al usuario
        Roles role1 = new Roles();
        role1.setRole(Role.EDITOR);
        role1.setUser(user);

        Roles role2 = new Roles();
        role2.setRole(Role.VISUALIZER);
        role2.setUser(user);

        List<Roles> roles = new ArrayList<>();
        roles.add(role1);
        roles.add(role2);

        // Mock del método findAllByUser del repositorio para devolver los roles configurados
        when(rolesRepository.findAllByUser(user)).thenReturn(roles);

        // Act
        // Llamar al método bajo prueba
        List<Roles> foundRoles = rolesService.findAllByUser(user);

        // Assert
        // Verificar que se encuentran todos los roles asociados al usuario
        assertNotNull(foundRoles);
        assertEquals(2, foundRoles.size());
    }

    @Test
    public void testFindById() {
        // Arrange
        // Configurar el ID del rol
        RolesId rolesId = new RolesId(1L, 1L);

        // Configurar el rol de prueba
        Roles role = new Roles();
        role.setRole(Role.EDITOR);

        // Mock del método findById del repositorio para devolver el rol configurado
        when(rolesRepository.findById(rolesId)).thenReturn(Optional.of(role));

        // Act
        // Llamar al método bajo prueba
        Roles foundRole = rolesService.findById(rolesId);

        // Assert
        // Verificar que se encuentra el rol por su ID
        assertNotNull(foundRole);
        assertEquals(Role.EDITOR, foundRole.getRole());
    }

    @Test
    public void testFindByUserUsernameAndAccountingId() {
        // Arrange
        // Configurar el nombre de usuario y el ID de la contabilidad
        String username = "example_user";
        Long accountingId = 1L;

        // Configurar el rol de prueba
        Roles role = new Roles();
        role.setRole(Role.EDITOR);

        // Mock del método findByUserUsernameAndAccountingId del repositorio para devolver el rol configurado
        when(rolesRepository.findByUserUsernameAndAccountingId(username, accountingId)).thenReturn(Optional.of(role));

        // Act
        // Llamar al método bajo prueba
        Roles foundRole = rolesService.findByUserUsernameAndAccountingId(username, accountingId);

        // Assert
        // Verificar que se encuentra el rol por nombre de usuario y ID de contabilidad
        assertNotNull(foundRole);
        assertEquals(Role.EDITOR, foundRole.getRole());
    }

}