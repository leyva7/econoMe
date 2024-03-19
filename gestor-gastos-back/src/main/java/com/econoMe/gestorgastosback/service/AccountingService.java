package com.econoMe.gestorgastosback.service;

import com.econoMe.gestorgastosback.common.Type;
import com.econoMe.gestorgastosback.model.Accounting;

import com.econoMe.gestorgastosback.model.Roles;
import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.repository.AccountingRepository;
import com.econoMe.gestorgastosback.repository.OperationsRepository;
import com.econoMe.gestorgastosback.repository.RolesRepository;
import com.econoMe.gestorgastosback.repository.UserRepository;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountingService {

    private final AccountingRepository accountingRepository;
    private final OperationsRepository operationsRepository;

    private final RolesRepository rolesRepository;
    private final UserService userService;


    private final RolesService rolesService;

    @Autowired
    public AccountingService(AccountingRepository accountingRepository, OperationsRepository operationsRepository, RolesRepository rolesRepository, RolesService rolesService, UserService userService){
        this.accountingRepository = accountingRepository;
        this.operationsRepository = operationsRepository;
        this.rolesRepository = rolesRepository;
        this.rolesService = rolesService;
        this.userService = userService;
    }
    public Accounting createAccounting(Accounting accounting){
        return accountingRepository.save(accounting);
    }

    public List<Accounting> findAllAccounting(){
        return accountingRepository.findAll();
    }

    public Accounting findAccountingById(Long id){
        return accountingRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No se encontró la contabilidad de ID: " + id));
    }

    public List<Accounting> findAccountingsSharedByUser(String username){
        List<Accounting> accountings = new ArrayList<>();

        List<Roles> roles = rolesRepository.findAllByUser(userService.getUserByUsername(username));
        List<Accounting> accountingUser = findAccountingByUserCreatorAndTypeShared(userService.getUserByUsername(username));

        for(int i = 0; i < roles.size(); i++){
            accountings.add(roles.get(i).getAccounting());
        }

        for(int i = 0; i < accountingUser.size(); i++){
            accountings.add(accountingUser.get(i));
        }

        return accountings;
    }

    public List<Accounting> findAccountingByUserCreatorAndTypeShared(User user){
        return accountingRepository.findByUserCreator(user)
                .stream()
                .filter(accounting -> accounting.getType() == Type.SHARED)
                .collect(Collectors.toList());
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
