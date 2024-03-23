package com.econoMe.gestorgastosback.service;

import com.econoMe.gestorgastosback.common.Role;
import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.Operations;
import com.econoMe.gestorgastosback.model.Roles;
import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.repository.OperationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OperationsService {

    @Autowired
    private final OperationsRepository operationsRepository;
    @Autowired
    private final RolesService rolesService;

    @Autowired
    public OperationsService(OperationsRepository operationsRepository, RolesService rolesService) {
        this.operationsRepository = operationsRepository;
        this.rolesService = rolesService;
    }

    public List<Operations> findAllOperations() {
        return operationsRepository.findAll();
    }

    public Operations findById(Long id) {
        return operationsRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No se encontró la operación de ID: " + id));
    }

    public List<Operations> findByAccounting(Accounting accounting) {
        List<Operations> operations = operationsRepository.findByAccounting(accounting);

        if (operations.isEmpty()) {
            return new ArrayList<>();
        } else {
            return operations;
        }
    }

    public List<Operations> findByUsername(User user) {
        List<Operations> operations = operationsRepository.findByUser(user);

        if (operations.isEmpty()) {
            throw new RuntimeException("La lista del usuario " + user.getUsername() + " está vacía");
        } else {
            return operations;
        }

    }

    public Operations createOperation(Operations operation) {

        if(rolesService.findByUserUsernameAndAccountingId(operation.getUser().getUsername(), operation.getAccounting().getId()).isEmpty()){
            throw new IllegalStateException("No existe ningún rol para el usuario " + operation.getUser().getUsername() + " y la contabilidad " + operation.getAccounting().getName()+ " no existe.");
        }

        Optional<Roles> rolesOptional = rolesService.findByUserUsernameAndAccountingId(operation.getUser().getUsername(), operation.getAccounting().getId());

        if (rolesOptional.isPresent()) {
            if(rolesOptional.get().getRole().equals(Role.EDITOR)){
                return operationsRepository.save(operation);
            }
            else{
                throw new RuntimeException("El usuario " + operation.getUser().getUsername() + " no tiene rol de editor en la contabilidad " + operation.getAccounting().getId());
            }

        } else {
            throw new RuntimeException("No se encontró la tupla para el usuario " + operation.getUser().getUsername() + " y la contabilidad " + operation.getAccounting().getId());
        }

    }

    public List<String> findAllAccountingCategories(Accounting accounting) {
        List<Operations> operationsAccounting = findByAccounting(accounting);
        Set<String> uniqueCategories = new HashSet<>();

        for (Operations operation : operationsAccounting) {
            uniqueCategories.add(operation.getCategory());
        }

        return new ArrayList<>(uniqueCategories);
    }

    public List<Operations> findAllUserOperationByAccounting(User user, Accounting accounting) {
        return operationsRepository.findByUserAndAccounting(user, accounting);
    }

    public List<Operations> findAllUserOperation(User user) {
        return operationsRepository.findByUser(user);
    }

    public Operations updateOperation(Long id, Operations operation) {
        if (id == null || !operationsRepository.existsById(id)) {
            throw new IllegalStateException("La operación con el ID " + operation.getId() + " no existe.");
        }
        return operationsRepository.save(operation);
    }

    public void updateUserOperation(User oldUser, User newUser) {
        List<Operations> operation = findAllUserOperation(oldUser);
        List<Operations> newOperation = new ArrayList<>();
        for(int i = 0; i < operation.size(); i++){
            newOperation.add(operation.get(i));
            newOperation.get(i).setUser(newUser);
            createOperation(newOperation.get(i));
        }
    }

    public void deleteOperation(Long id) {
        if (!operationsRepository.existsById(id)) {
            throw new IllegalStateException("La operación con el ID " + id + " no existe.");
        }
        operationsRepository.deleteById(id);
    }

    public void deleteByAccounting(Accounting accounting) {
        if (!operationsRepository.existsByAccounting(accounting)) {
            throw new IllegalStateException("La operación con la contabilidad " + accounting.getName() + " no existe.");
        }
        operationsRepository.deleteByAccounting(accounting);
    }

    public void deleteByUser(User user) {
        if (!operationsRepository.existsByUser(user)) {
            throw new IllegalStateException("La operación con el usuario " + user.getUsername() + " no existe.");
        }
        operationsRepository.deleteByUser(user);
    }

}
