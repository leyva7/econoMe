package com.econoMe.gestorgastosback.service;

import com.econoMe.gestorgastosback.dto.UserDto;
import com.econoMe.gestorgastosback.model.User;
import org.springframework.stereotype.Service;

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
}
