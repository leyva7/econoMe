package com.econoMe.gestorgastosback.service;

import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.Operations;
import com.econoMe.gestorgastosback.model.Roles;
import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.repository.AccountingRepository;
import com.econoMe.gestorgastosback.repository.OperationsRepository;
import com.econoMe.gestorgastosback.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class OperationsService {

    private final OperationsRepository operationsRepository;
    private final RolesRepository rolesRepository;

    @Autowired
    public OperationsService(OperationsRepository operationsRepository, RolesRepository rolesRepository, RolesService rolesService) {
        this.operationsRepository = operationsRepository;
        this.rolesRepository = rolesRepository;
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

        if(rolesRepository.findByUserUsernameAndAccountingId(operation.getUser().getUsername(), operation.getAccounting().getId()).isEmpty()){
            throw new IllegalStateException("No existe ningún rol para el usuario " + operation.getUser().getUsername() + " y la contabilidad " + operation.getAccounting().getName()+ " no existe.");
        }

        Optional<Roles> rolesOptional = rolesRepository.findByUserUsernameAndAccountingId(operation.getUser().getUsername(), operation.getAccounting().getId());

        if (rolesOptional.isPresent()) {
            if(rolesOptional.get().getRole().equals("EDITOR")){
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
        List<String> categories = new ArrayList<>();
        List<Operations> operationsAccounting = findByAccounting(accounting);

        for(int i = 0; i < operationsAccounting.size(); i++){
            categories.add(operationsAccounting.get(i).getCategory());
        }

        return categories;
    }

    public Operations updateOperation(Long id, Operations operation) {
        if (id == null || !operationsRepository.existsById(id)) {
            throw new IllegalStateException("La operación con el ID " + operation.getId() + " no existe.");
        }
        return operationsRepository.save(operation);
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
