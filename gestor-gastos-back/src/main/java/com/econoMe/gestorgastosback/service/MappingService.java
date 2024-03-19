package com.econoMe.gestorgastosback.service;

import com.econoMe.gestorgastosback.dto.AccountingDto;
import com.econoMe.gestorgastosback.dto.UserDto;
import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MappingService {

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
        accountingDto.setUserCreator(accounting.getName());

        return accountingDto;
    }

    public List<AccountingDto> accountingDtoList(List<Accounting> accountingList){
        List<AccountingDto> accountingDtoList = new ArrayList<>();

        for(int i=0; i<accountingList.size(); i++){
            accountingDtoList.add(accountingToDto(accountingList.get(i)));
        }

        return accountingDtoList;
    }
}
