package com.econoMe.gestorgastosback.service;

import com.econoMe.gestorgastosback.exception.RoleException;
import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.Roles;
import com.econoMe.gestorgastosback.model.RolesId;
import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolesService {

    private final RolesRepository rolesRepository;

    @Autowired
    public RolesService(RolesRepository rolesRepository) {
        this.rolesRepository = rolesRepository;
    }

    /**
     * Crea un nuevo rol y lo guarda en la base de datos.
     *
     * @param role Rol a crear.
     * @return Rol creado y guardado.
     */
    public Roles createRole(Roles role) {
        return rolesRepository.save(role);
    }

    /**
     * Encuentra todos los roles asociados a un usuario dado.
     *
     * @param user Usuario para el cual se buscan los roles.
     * @return Lista de roles asociados al usuario.
     */
    public List<Roles> findAllByUser(User user) {
        return rolesRepository.findAllByUser(user);
    }

    /**
     * Crea el primer rol para una contabilidad.
     *
     * @param role Rol a crear.
     * @return Rol creado y guardado.
     */
    public Roles createFirstRole(Roles role) {
        return rolesRepository.save(role);
    }

    /**
     * Actualiza un rol existente.
     *
     * @param rolesId      Identificador del rol a actualizar.
     * @param updatedRole  Rol con los datos actualizados.
     * @return Rol actualizado y guardado.
     */
    public Roles updateRole(RolesId rolesId, Roles updatedRole) {
        Roles existingRole = findById(rolesId);
        existingRole.setRole(updatedRole.getRole());
        return rolesRepository.save(existingRole);
    }

    /**
     * Encuentra un rol por su identificador.
     *
     * @param rolesId Identificador del rol.
     * @return Rol encontrado.
     * @throws RoleException si no se encuentra el rol con el ID especificado.
     */
    public Roles findById(RolesId rolesId) {
        return rolesRepository.findById(rolesId)
                .orElseThrow(() -> new RoleException("No se encontró el rol con el ID especificado."));
    }

    /**
     * Encuentra un rol por nombre de usuario y ID de contabilidad.
     *
     * @param username     Nombre de usuario.
     * @param accountingId ID de contabilidad.
     * @return Rol encontrado.
     * @throws RoleException si no se encuentra el rol con el nombre de usuario y ID de contabilidad especificados.
     */
    public Roles findByUserUsernameAndAccountingId(String username, Long accountingId) {
        return rolesRepository.findByUserUsernameAndAccountingId(username, accountingId)
                .orElseThrow(() -> new RoleException("Se produjo un error al buscar roles por nombre de usuario " + username + " y ID de contabilidad " + accountingId));
    }

    /**
     * Encuentra todos los roles asociados a una contabilidad dada.
     *
     * @param accounting Contabilidad para la cual se buscan los roles.
     * @return Lista de roles asociados a la contabilidad.
     */
    public List<Roles> findAllByAccounting(Accounting accounting) {
        return rolesRepository.findAllByAccounting(accounting);
    }

    /**
     * Elimina un rol por su identificador.
     *
     * @param rolesId Identificador del rol a eliminar.
     */
    public void deleteRole(RolesId rolesId) {
        rolesRepository.delete(findById(rolesId));
    }

    /**
     * Elimina todos los roles asociados a una contabilidad.
     *
     * @param accounting Contabilidad de la cual se eliminarán los roles.
     */
    public void deleteByAccounting(Accounting accounting) {
        rolesRepository.deleteByAccounting(accounting);
    }
}
