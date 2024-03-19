package com.econoMe.gestorgastosback.dto;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDto {
    private String username;
    private String name;
    private String surname;
    private String mail;

}
