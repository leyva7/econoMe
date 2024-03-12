package com.econoMe.gestorgastosback.service;

import com.econoMe.gestorgastosback.model.Accounting;

import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.repository.AccountingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AccountingService {

    private final AccountingRepository accountingRepository;

    @Autowired
    public AccountingService(AccountingRepository accountingRepository){
        this.accountingRepository = accountingRepository;
    }
    public Accounting createAccounting(Accounting accounting){
        return accountingRepository.save(accounting);
    }

    public List<Accounting> getAllAccounting(){
        return accountingRepository.findAll();
    }

    public Accounting getAccountingByID(Long id){
        return accountingRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No se encontró el usuario de ID: " + id));
    }


    public List<Accounting> getAccountingByUser(User user){
        return accountingRepository.findByUserCreator(user);
    }

    public Accounting updateAccounting(Long id, Accounting accounting){
        if(id == null || !accountingRepository.existsById(id)){
            throw new IllegalStateException("La cuenta con el ID " + id + " no existe.");
        }

        return accountingRepository.save(accounting);
    }

    public void deleteAccounting(Long id){
        if (!accountingRepository.existsById(id)) {
            throw new IllegalStateException("La cuenta con el ID proporcionado no existe.");
        }
        accountingRepository.deleteById(id);
    }


}
