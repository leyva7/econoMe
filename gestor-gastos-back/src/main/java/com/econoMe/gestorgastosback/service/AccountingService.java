package com.econoMe.gestorgastosback.service;

import com.econoMe.gestorgastosback.common.Role;
import com.econoMe.gestorgastosback.common.Type;
import com.econoMe.gestorgastosback.exception.AccountingException;
import com.econoMe.gestorgastosback.exception.RoleException;
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
    public AccountingService(AccountingRepository accountingRepository, OperationsService operationsService, RolesService rolesService){
        this.accountingRepository = accountingRepository;
        this.operationsService = operationsService;
        this.rolesService = rolesService;
    }
    @Transactional
    public Accounting createAccounting(Accounting accounting) {
        Accounting savedAccounting = accountingRepository.save(accounting);
        rolesService.createRole(new Roles(accounting.getUserCreator(), accounting, Role.EDITOR));
        return savedAccounting;
    }

    @Transactional
    public Accounting createFirstAccounting(Accounting accounting){
        Accounting savedAccounting = accountingRepository.save(accounting);
        rolesService.createFirstRole(new Roles(accounting.getUserCreator(), accounting, Role.EDITOR));
        return savedAccounting;
    }

    public Accounting findAccountingById(Long id){
        return accountingRepository.findById(id).orElseThrow(() -> new AccountingException("No se encontró la contabilidad de ID: " + id));
    }

    public List<Accounting> findAllUserAccounting(User user) {
        List<Accounting> accountings = findAccountingsSharedByUser(user);
        findPersonalAccounting(user).ifPresent(accountings::add);
        return accountings;
    }

    public List<Accounting> findAccountingsSharedByUser(User user){

        List<Accounting> accountingUser = findAccountingByTypeShared(user);

        return new ArrayList<>(accountingUser);
    }

    public List<Accounting> findAccountingByTypeShared(User user) {
        List<Roles> roles = rolesService.findAllByUser(user);
        List<Accounting> accountings = new ArrayList<>();

        for (Roles role : roles) {
            accountings.add(role.getAccounting());
        }

        return accountings.stream()
                .filter(accounting -> accounting.getType() == Type.SHARED)
                .collect(Collectors.toList());
    }

    public Optional<Accounting> findPersonalAccounting(User user) {
        Optional<Accounting> personalAccountingOptional = accountingRepository.findByUserCreator(user)
                .stream().filter(accounting -> accounting.getType() == Type.PERSONAL)
                .findFirst();

        if (personalAccountingOptional.isEmpty()) {
            throw new AccountingException("No se encontró la contabilidad personal para el usuario: " + user.getUsername());
        }

        return personalAccountingOptional;
    }


    public Accounting updateAccounting(Long id, User userCreator, String newName, String newDescription){
        if(id == null || !accountingRepository.existsById(id)){
            throw new AccountingException("La contabilidad con el ID " + id+ " no existe.");
        }

        Accounting accounting = findAccountingById(id);

        if(userCreator.equals(accounting.getUserCreator())){
            accounting.setName(newName);
            accounting.setDescription(newDescription);
            return accountingRepository.save(accounting);
        }
        else{
            throw new AccountingException("El usuario " + userCreator.getUsername() + " no es el creador de la contabilidad " + accounting.getId());
        }

    }

    @Transactional
    public void deleteAccounting(Long id, User user){
        Accounting accounting = findAccountingById(id);

        if(!accounting.getUserCreator().equals(user)){
            throw new AccountingException("No es ud el creador de la contabilidad.");
        }

        operationsService.deleteByAccounting(accounting);
        rolesService.deleteByAccounting(accounting);
        accountingRepository.deleteById(id);
    }

}
