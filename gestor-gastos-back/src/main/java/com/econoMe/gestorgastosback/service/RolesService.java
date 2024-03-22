package com.econoMe.gestorgastosback.service;

import com.econoMe.gestorgastosback.common.Type;
import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.Roles;
import com.econoMe.gestorgastosback.model.RolesId;
import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.repository.AccountingRepository;
import com.econoMe.gestorgastosback.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import com.econoMe.gestorgastosback.repository.RolesRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class RolesService {

    private final RolesRepository rolesRepository;
    private final UserRepository userRepository;
    private final AccountingRepository accountingRepository;

    @Autowired
    public RolesService(RolesRepository rolesRepository, UserRepository userRepository, AccountingRepository accountingRepository) {
        this.rolesRepository = rolesRepository;
        this.userRepository = userRepository;
        this.accountingRepository = accountingRepository;
    }

    public Roles createRole(Roles role) {
        String userUsername = role.getUser().getUsername();
        Long accountingId = role.getAccounting().getId();

        if (!validateUserAndAccountingExistence(userUsername, accountingId)) {
            throw new EntityNotFoundException("Usuario o Contabilidad no existen.");
        }

        Optional<Roles> existingRole = rolesRepository.findByUserUsernameAndAccountingId(userUsername, accountingId);
        if (existingRole.isPresent()) {
            throw new IllegalStateException("El rol para este usuario y contabilidad ya existe.");
        }

        // Si todo está correcto, guarda el nuevo rol
        return rolesRepository.save(role);
    }

    public List<Roles> findAllByUser(User user) {
        return rolesRepository.findAllByUser(user);
    }

    public Roles createFirstRole(Roles role) {
        return rolesRepository.save(role);
    }

    public Optional<Roles> updateRole(RolesId rolesId, Roles updatedRole) {
        // Comprueba si el rol existe antes de actualizarlo
        return rolesRepository.findById(rolesId).map(existingRole -> {
            existingRole.setRole(updatedRole.getRole());
            return rolesRepository.save(existingRole);
        });
    }

    public boolean deleteRole(RolesId rolesId) {
        return rolesRepository.findById(rolesId).map(role -> {
            rolesRepository.delete(role);
            return true;
        }).orElse(false);
    }

    public Optional<Roles> findRoleById(RolesId rolesId) {
        return rolesRepository.findById(rolesId);
    }

    public List<Roles> findAllRoles() {
        return rolesRepository.findAll();
    }

    public Optional<Roles> findByUserUsernameAndAccountingId(String username, Long accountingId){
        try {
            return rolesRepository.findByUserUsernameAndAccountingId(username, accountingId);
        } catch (DataAccessException e) {
            // Manejar la excepción de acceso a datos de Spring
            throw new RuntimeException("Se produjo un error al buscar roles por nombre de usuario y ID de contabilidad", e);
        }
    }

    public boolean validateUserAndAccountingExistence(String userUsername, Long accountingId) {
        boolean userExists = userRepository.existsById(userUsername);
        boolean accountingExists = accountingRepository.existsById(accountingId);
        return userExists && accountingExists;
    }

    public void deleteByAccounting(Accounting accounting) {

        rolesRepository.deleteByAccounting(accounting);
    }


}
