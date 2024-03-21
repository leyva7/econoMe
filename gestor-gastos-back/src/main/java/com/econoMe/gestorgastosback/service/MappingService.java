package com.econoMe.gestorgastosback.service;

import com.econoMe.gestorgastosback.dto.AccountingDto;
import com.econoMe.gestorgastosback.dto.OperationsDto;
import com.econoMe.gestorgastosback.dto.RolesDto;
import com.econoMe.gestorgastosback.dto.UserDto;
import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.Operations;
import com.econoMe.gestorgastosback.model.Roles;
import com.econoMe.gestorgastosback.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MappingService {

    @Autowired UserService userService;

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
        accountingDto.setName(accounting.getName());
        accountingDto.setDescription(accounting.getDescription());
        accountingDto.setType(accounting.getType());
        accountingDto.setUserCreator(accounting.getUserCreator().getName());

        return accountingDto;
    }

    public Accounting accountingDtoToAccounting(AccountingDto accountingDto){
        Accounting accounting = new Accounting();
        accounting.setName(accountingDto.getName());
        accounting.setDescription(accountingDto.getDescription());
        accounting.setType(accountingDto.getType());
        accounting.setUserCreator(userService.getUserByUsername(accountingDto.getUserCreator()));

        return accounting;
    }

    public List<AccountingDto> accountingDtoList(List<Accounting> accountingList){
        List<AccountingDto> accountingDtoList = new ArrayList<>();

        for(int i=0; i<accountingList.size(); i++){
            accountingDtoList.add(accountingToDto(accountingList.get(i)));
        }

        return accountingDtoList;
    }

    public RolesDto rolesToDto(Roles roles){
       RolesDto role = new RolesDto();
       role.setRole(roles.getRole());

       return role;
    }

    public OperationsDto operationsToDto(Operations operations){
        OperationsDto operationsDto = new OperationsDto();
        operationsDto.setDate(operations.getDate());
        operationsDto.setQuantity(operations.getQuantity());
        operationsDto.setCategory(operations.getCategory());
        operationsDto.setAccountingName(operations.getAccounting().getName());
        operationsDto.setDescription(operations.getDescription());
        operationsDto.setType(operations.getType());
        operationsDto.setUsername(operations.getUser().getUsername());

        return operationsDto;
    }
}
