package com.econoMe.gestorgastosback.service;

import com.econoMe.gestorgastosback.dto.RegistrationDto;
import com.econoMe.gestorgastosback.dto.UserDto;
import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountingService accountingService;

    @Autowired
    private MappingService mappingService;

    @Transactional
    public UserDto registerUserAndAccounting(RegistrationDto registrationDto) {
        User newUser = registrationDto.getUser();

        User savedUser = userService.createUser(newUser);

        Accounting newAccounting = mappingService.accountingDtoToAccounting(registrationDto.getAccounting());
        newAccounting.setUserCreator(savedUser);

        accountingService.createFirstAccounting(newAccounting);

        return mappingService.userToDto(savedUser);
    }
}
