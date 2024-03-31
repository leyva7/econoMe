package com.econoMe.gestorgastosback.service;

import com.econoMe.gestorgastosback.common.Role;
import com.econoMe.gestorgastosback.common.Type;
import com.econoMe.gestorgastosback.model.Accounting;

import com.econoMe.gestorgastosback.model.Operations;
import com.econoMe.gestorgastosback.model.Roles;
import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.repository.AccountingRepository;
import com.econoMe.gestorgastosback.repository.OperationsRepository;
import com.econoMe.gestorgastosback.repository.RolesRepository;
import com.econoMe.gestorgastosback.repository.UserRepository;
import jakarta.persistence.Id;
import jakarta.transaction.Transactional;
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
    private final OperationsService operationsService;
    private final RolesRepository rolesRepository;
    private final RolesService rolesService;

    @Autowired
    public AccountingService(AccountingRepository accountingRepository, OperationsService operationsService, RolesRepository rolesRepository, RolesService rolesService){
        this.accountingRepository = accountingRepository;
        this.operationsService = operationsService;
        this.rolesRepository = rolesRepository;
        this.rolesService = rolesService;
    }
    @Transactional
    public Accounting createAccounting(Accounting accounting){
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

    public List<Accounting> findAllAccounting(){
        return accountingRepository.findAll();
    }

    public Accounting findAccountingById(Long id){
        return accountingRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No se encontró la contabilidad de ID: " + id));
    }

    public List<Accounting> findAllUserAccounting(User user){
        List<Accounting> accountings = findAccountingsSharedByUser(user);
        Optional<Accounting> personalAccountingOptional = findPersonalAccounting(user);
        if (personalAccountingOptional.isPresent()) {
            Accounting personalAccounting = personalAccountingOptional.get();
            accountings.add(personalAccounting);
        } else {
            throw new NoSuchElementException("No existe ese elemento");
        }

        return accountings;

    }

    public List<Accounting> findAccountingsSharedByUser(User user){
        List<Accounting> accountings = new ArrayList<Accounting>();

        List<Accounting> accountingUser = findAccountingByTypeShared(user);

        for(int i = 0; i < accountingUser.size(); i++){
            accountings.add(accountingUser.get(i));
        }

        return accountings;
    }

    public List<Accounting> findAccountingByTypeShared(User user) {
        List<Roles> roles = rolesService.findAllByUser(user);
        List<Accounting> accountings = new ArrayList<>();

        for (int i = 0; i < roles.size(); i++) {
            accountings.add(roles.get(i).getAccounting());
        }

        return accountings.stream()
                .filter(accounting -> accounting.getType() == Type.SHARED)
                .collect(Collectors.toList());
    }

    public Optional<Accounting> findPersonalAccounting(User user) {
        Optional<Accounting> personalAccountingOptional = accountingRepository.findByUserCreator(user)
                .stream()
                .filter(accounting -> accounting.getType() == Type.PERSONAL)
                .findFirst();

        if (personalAccountingOptional.isPresent()) {
            return personalAccountingOptional;
        } else {
            throw new NoSuchElementException("No se encontró la contabilidad personal para el usuario: " + user.getUsername());
        }
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

    @Transactional
    public void deleteAccounting(Long id, User user){
        if (!accountingRepository.existsById(id)) {
            throw new IllegalStateException("La contabilidad con el ID proporcionado no existe.");
        }


        if(!accountingRepository.getById(id).getUserCreator().equals(user)){
            throw new IllegalStateException("No es ud el creador de la contabilidad.");
        }

        operationsService.deleteByAccounting(accountingRepository.getById(id));
        rolesService.deleteByAccounting(findAccountingById(id));
        accountingRepository.deleteById(id);
    }

    public void deleteByUserCreator(User user){
        accountingRepository.deleteByUserCreator(user);
    }

}
