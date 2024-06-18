package com.econoMe.gestorgastosback.service;

import com.econoMe.gestorgastosback.common.Role;
import com.econoMe.gestorgastosback.common.Type;
import com.econoMe.gestorgastosback.exception.AccountingException;
import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.Roles;
import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.repository.AccountingRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountingService {

    private final AccountingRepository accountingRepository;
    private final OperationsService operationsService;
    private final RolesService rolesService;

    @Autowired
    public AccountingService(AccountingRepository accountingRepository, OperationsService operationsService, RolesService rolesService) {
        this.accountingRepository = accountingRepository;
        this.operationsService = operationsService;
        this.rolesService = rolesService;
    }

    // Método para crear una nueva contabilidad
    @Transactional
    public Accounting createAccounting(Accounting accounting) {
        // Guarda la contabilidad en la base de datos
        Accounting savedAccounting = accountingRepository.save(accounting);
        // Crea un rol para el creador de la contabilidad (puede ser un editor u otro rol según la lógica de negocio)
        rolesService.createRole(new Roles(accounting.getUserCreator(), accounting, Role.EDITOR));
        return savedAccounting; // Devuelve la contabilidad guardada
    }

    // Método para crear la primera contabilidad (posiblemente con un rol especial)
    @Transactional
    public Accounting createFirstAccounting(Accounting accounting) {
        Accounting savedAccounting = accountingRepository.save(accounting);
        // Aquí puedes definir una lógica específica para la creación del primer rol, como un rol de administrador
        rolesService.createFirstRole(new Roles(accounting.getUserCreator(), accounting, Role.EDITOR));
        return savedAccounting;
    }

    // Método para encontrar una contabilidad por su ID
    public Accounting findAccountingById(Long id) {
        return accountingRepository.findById(id).orElseThrow(() -> new AccountingException("No se encontró la contabilidad de ID: " + id));
    }

    // Método para encontrar todas las contabilidades compartidas por un usuario
    public List<Accounting> findAllUserAccounting(User user) {
        // Obtiene las contabilidades compartidas por el usuario
        List<Accounting> accountings = findAccountingsSharedByUser(user);
        // Obtiene la contabilidad personal del usuario si existe y la agrega a la lista
        findPersonalAccounting(user).ifPresent(accountings::add);
        return accountings; // Devuelve la lista de contabilidades encontradas
    }

    // Método para encontrar todas las contabilidades compartidas por un usuario
    public List<Accounting> findAccountingsSharedByUser(User user) {
        // Obtiene los roles del usuario
        List<Roles> roles = rolesService.findAllByUser(user);
        // Obtiene las contabilidades de los roles filtrando por tipo compartido
        return roles.stream()
                .map(Roles::getAccounting)
                .filter(accounting -> accounting.getType() == Type.SHARED)
                .collect(Collectors.toList());
    }

    // Método para encontrar la contabilidad personal de un usuario si existe
    public Optional<Accounting> findPersonalAccounting(User user) {
        return accountingRepository.findByUserCreator(user)
                .stream()
                .filter(accounting -> accounting.getType() == Type.PERSONAL)
                .findFirst();
    }

    // Método para actualizar una contabilidad
    @Transactional
    public Accounting updateAccounting(Long id, User userCreator, String newName, String newDescription) {
        // Verifica si la contabilidad existe
        if (id == null || !accountingRepository.existsById(id)) {
            throw new AccountingException("La contabilidad con el ID " + id + " no existe.");
        }

        // Obtiene la contabilidad por su ID
        Accounting accounting = findAccountingById(id);

        // Verifica si el usuario actual es el creador de la contabilidad
        if (userCreator.equals(accounting.getUserCreator())) {
            // Actualiza el nombre y la descripción de la contabilidad
            accounting.setName(newName);
            accounting.setDescription(newDescription);
            return accountingRepository.save(accounting); // Guarda los cambios y devuelve la contabilidad actualizada
        } else {
            throw new AccountingException("El usuario " + userCreator.getUsername() + " no es el creador de la contabilidad " + accounting.getId());
        }
    }

    // Método para eliminar una contabilidad
    @Transactional
    public void deleteAccounting(Long id, User user) {
        // Verifica si la contabilidad existe
        Accounting accounting = findAccountingById(id);

        // Verifica si el usuario actual es el creador de la contabilidad
        if (!accounting.getUserCreator().equals(user)) {
            throw new AccountingException("No es usted el creador de la contabilidad.");
        }

        // Elimina las operaciones asociadas a la contabilidad
        operationsService.deleteByAccounting(accounting);
        // Elimina los roles asociados a la contabilidad
        rolesService.deleteByAccounting(accounting);
        // Elimina la contabilidad de la base de datos
        accountingRepository.deleteById(id);
    }
}
