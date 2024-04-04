package com.econoMe.gestorgastosback.service;

import com.econoMe.gestorgastosback.dto.*;
import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.Operations;
import com.econoMe.gestorgastosback.model.Roles;
import com.econoMe.gestorgastosback.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MappingService {

    private final UserService userService;
    private final AccountingService accountingService;

    MappingService(UserService userService, AccountingService accountingService){
        this.userService = userService;
        this.accountingService = accountingService;
    }

    public UserDto userToDto(User user) {
        UserDto dto = new UserDto();
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setMail(user.getMail());
        return dto;
    }

    public AccountingDto accountingToDto(Accounting accounting){
        AccountingDto accountingDto = new AccountingDto();
        accountingDto.setId(accounting.getId());
        accountingDto.setName(accounting.getName());
        accountingDto.setDescription(accounting.getDescription());
        accountingDto.setType(accounting.getType());
        accountingDto.setUserCreator(accounting.getUserCreator().getName());

        return accountingDto;
    }

    public Accounting accountingDtoToAccounting(AccountingDto accountingDto){
        Accounting accounting = new Accounting();
        accounting.setId(accountingDto.getId());
        accounting.setName(accountingDto.getName());
        accounting.setDescription(accountingDto.getDescription());
        accounting.setType(accountingDto.getType());

        return accounting;
    }

    public Accounting accountingRegisterToAccounting(AccountingRegistration accountingRegistration){
        Accounting accounting = new Accounting();
        accounting.setName(accountingRegistration.getName());
        accounting.setDescription(accountingRegistration.getDescription());
        accounting.setType(accountingRegistration.getType());
        accounting.setUserCreator(userService.getUserByUsername(accountingRegistration.getUserCreator()));

        return accounting;
    }

    public List<AccountingDto> accountingDtoList(List<Accounting> accountingList){
        List<AccountingDto> accountingDtoList = new ArrayList<>();

        for (Accounting accounting : accountingList) {
            accountingDtoList.add(accountingToDto(accounting));
        }

        return accountingDtoList;
    }

    public RolesDto rolesToDto(Roles roles){
       RolesDto role = new RolesDto();
       role.setRole(roles.getRole());
       role.setUsername(roles.getUser().getUsername());
       role.setAccountingId(roles.getAccounting().getId());

       return role;
    }

    public List<RolesDto> rolesListToDto(List<Roles> roles){
        List<RolesDto> rolesDtoList = new ArrayList<>();

        for (Roles role : roles) {
            rolesDtoList.add(rolesToDto(role));
        }

        return rolesDtoList;
    }

    public OperationsDto operationsToDto(Operations operations){
        OperationsDto operationsDto = new OperationsDto();
        operationsDto.setId(operations.getId());
        operationsDto.setDate(operations.getDate());
        operationsDto.setQuantity(operations.getQuantity());
        operationsDto.setCategory(operations.getCategory());
        operationsDto.setAccountingId(operations.getAccounting().getId());
        operationsDto.setDescription(operations.getDescription());
        operationsDto.setType(operations.getType());
        operationsDto.setUsername(operations.getUser().getUsername());

        return operationsDto;
    }
    public List<OperationsDto> operationListToDto(List<Operations> operations){
        List<OperationsDto> operationsDto = new ArrayList<>();
        for (Operations operation : operations) {
            operationsDto.add(operationsToDto(operation));
        }

        return operationsDto;
    }

    public Operations dtoToOperation(OperationsDto operationsDto){
        Operations operations = new Operations();
        operations.setId(operationsDto.getId());
        operations.setDate(operationsDto.getDate());
        operations.setQuantity(operationsDto.getQuantity());
        operations.setCategory(operationsDto.getCategory());
        operations.setAccounting(accountingService.findAccountingById(operationsDto.getAccountingId()));
        operations.setDescription(operationsDto.getDescription());
        operations.setType(operationsDto.getType());
        operations.setUser(userService.getUserByUsername(operationsDto.getUsername()));

        return operations;
    }


}
