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
        // Configuración del rol
        Roles role = new Roles();
        role.setRole(Role.EDITOR);

        // Mock del método save del repositorio
        when(rolesRepository.save(any(Roles.class))).thenReturn(role);

        // Crear el rol y verificar que se devuelve correctamente
        Roles createdRole = rolesService.createRole(role);
        assertNotNull(createdRole);
        assertEquals(Role.EDITOR, createdRole.getRole());
    }

    @Test
    public void testFindAllByUser() {
        // Configuración del usuario
        User user = new User();
        user.setId(1L);

        // Configuración de roles
        Roles role1 = new Roles();
        role1.setRole(Role.EDITOR);
        role1.setUser(user);

        Roles role2 = new Roles();
        role2.setRole(Role.VISUALIZER);
        role2.setUser(user);

        List<Roles> roles = new ArrayList<>();
        roles.add(role1);
        roles.add(role2);

        // Mock del método findAllByUser del repositorio
        when(rolesRepository.findAllByUser(user)).thenReturn(roles);

        // Obtener todos los roles asociados al usuario y verificar que se devuelven correctamente
        List<Roles> foundRoles = rolesService.findAllByUser(user);
        assertNotNull(foundRoles);
        assertEquals(2, foundRoles.size());
    }

    @Test
    public void testFindById() {
        // Configuración del ID del rol
        RolesId rolesId = new RolesId(1L, 1L);

        // Configuración del rol
        Roles role = new Roles();
        role.setRole(Role.EDITOR);

        // Mock del método findById del repositorio
        when(rolesRepository.findById(rolesId)).thenReturn(Optional.of(role));

        // Obtener el rol por ID y verificar que se devuelve correctamente
        Roles foundRole = rolesService.findById(rolesId);
        assertNotNull(foundRole);
        assertEquals(Role.EDITOR, foundRole.getRole());
    }

    @Test
    public void testFindByUserUsernameAndAccountingId() {
        // Configuración del nombre de usuario y el ID de la contabilidad
        String username = "example_user";
        Long accountingId = 1L;

        // Configuración del rol
        Roles role = new Roles();
        role.setRole(Role.EDITOR);

        // Mock del método findByUserUsernameAndAccountingId del repositorio
        when(rolesRepository.findByUserUsernameAndAccountingId(username, accountingId)).thenReturn(Optional.of(role));

        // Obtener el rol por nombre de usuario y ID de contabilidad y verificar que se devuelve correctamente
        Roles foundRole = rolesService.findByUserUsernameAndAccountingId(username, accountingId);
        assertNotNull(foundRole);
        assertEquals(Role.EDITOR, foundRole.getRole());
    }

}
