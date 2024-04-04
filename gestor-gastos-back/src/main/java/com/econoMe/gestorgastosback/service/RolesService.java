package com.econoMe.gestorgastosback.service;


import com.econoMe.gestorgastosback.exception.RoleException;
import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.Roles;
import com.econoMe.gestorgastosback.model.RolesId;
import com.econoMe.gestorgastosback.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.econoMe.gestorgastosback.repository.RolesRepository;


import java.util.List;

@Service
public class RolesService {

    private final RolesRepository rolesRepository;

    @Autowired
    public RolesService(RolesRepository rolesRepository) {
        this.rolesRepository = rolesRepository;
    }

    public Roles createRole(Roles role) {
        String userUsername = role.getUser().getUsername();
        Long accountingId = role.getAccounting().getId();

        rolesRepository.findByUserUsernameAndAccountingId(userUsername, accountingId).orElseThrow(() -> new RoleException("El rol para este usuario y contabilidad ya existe."));

        return rolesRepository.save(role);
    }


    public List<Roles> findAllByUser(User user) {
        return rolesRepository.findAllByUser(user);
    }

    public Roles createFirstRole(Roles role) {
        return rolesRepository.save(role);
    }

    public Roles updateRole(RolesId rolesId, Roles updatedRole) {
        Roles existingRole = findById(rolesId);
        existingRole.setRole(updatedRole.getRole());
        return rolesRepository.save(existingRole);
    }

    public Roles findById(RolesId rolesId) {
        return rolesRepository.findById(rolesId)
                .orElseThrow(() -> new RoleException("No se encontró el rol con el ID especificado."));
    }

    public Roles findByUserUsernameAndAccountingId(String username, Long accountingId){
        return rolesRepository.findByUserUsernameAndAccountingId(username, accountingId).orElseThrow(() -> new RoleException("Se produjo un error al buscar roles por nombre de usuario " + username + " y ID de contabilidad " + accountingId));
    }

    public List<Roles> findAllAccountingUsersNotCreator(Accounting accounting) {
        return rolesRepository.findAllByAccountingAndUserNot(accounting, accounting.getUserCreator());
    }

    public void deleteRole(RolesId rolesId) {
        rolesRepository.delete(findById(rolesId));
    }

    public void deleteByAccounting(Accounting accounting) {
        rolesRepository.deleteByAccounting(accounting);
    }

}
