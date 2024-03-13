package com.econoMe.gestorgastosback.service;

import com.econoMe.gestorgastosback.model.Accounting;

import com.econoMe.gestorgastosback.model.Roles;
import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.repository.AccountingRepository;
import com.econoMe.gestorgastosback.repository.OperationsRepository;
import com.econoMe.gestorgastosback.repository.RolesRepository;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AccountingService {

    private final AccountingRepository accountingRepository;
    private final OperationsRepository operationsRepository;

    private final RolesRepository rolesRepository;

    private final RolesService rolesService;

    @Autowired
    public AccountingService(AccountingRepository accountingRepository, OperationsRepository operationsRepository, RolesRepository rolesRepository, RolesService rolesService){
        this.accountingRepository = accountingRepository;
        this.operationsRepository = operationsRepository;
        this.rolesRepository = rolesRepository;
        this.rolesService = rolesService;
    }
    public Accounting createAccounting(Accounting accounting){
        return accountingRepository.save(accounting);
    }

    public List<Accounting> findAllAccounting(){
        return accountingRepository.findAll();
    }

    public Accounting findAccountingById(Long id){
        return accountingRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No se encontró el usuario de ID: " + id));
    }


    public List<Accounting> findAccountingByUser(User user){
        return accountingRepository.findByUserCreator(user);
    }

    public Accounting updateAccounting(String username, Accounting accounting){
        if(accounting.getId() == null || !accountingRepository.existsById(accounting.getId())){
            throw new IllegalStateException("La contabilidad con el ID " + accounting.getId() + " no existe.");
        }

        if(rolesRepository.findByUserUsernameAndAccountingId(username, accounting.getId()).isEmpty()){
            throw new IllegalStateException("La contabilidad con el ID " + accounting.getId() + "y el usuario " + username + " no existe.");
        }

        Optional<Roles> rolesOptional = rolesRepository.findByUserUsernameAndAccountingId(username, accounting.getId());

        if (rolesOptional.isPresent()) {
            if(rolesOptional.get().getRole().equals("EDITOR")){
                return accountingRepository.save(accounting);
            }
            else{
                throw new RuntimeException("El usuario " + username + " no tiene rol de editor en la contabilidad " + accounting.getId());
            }

        } else {
            throw new RuntimeException("No se encontró la tupla para el usuario " + username + " y la contabilidad " + accounting.getId());
        }

    }

    public void deleteAccounting(Long id, User user){
        if (!accountingRepository.existsById(id)) {
            throw new IllegalStateException("La contabilidad con el ID proporcionado no existe.");
        }

        if(accountingRepository.getById(id).getUserCreator().equals(user)){
            throw new IllegalStateException("No es ud el creador de la contabilidad.");
        }

        operationsRepository.deleteByAccounting(accountingRepository.getById(id));
        accountingRepository.deleteById(id);
    }



}
