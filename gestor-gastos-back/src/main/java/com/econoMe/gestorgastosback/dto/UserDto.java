package com.econoMe.gestorgastosback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String username;  // Nombre de usuario

    private String name;      // Nombre del usuario

    private String surname;   // Apellido del usuario

    private String mail;      // Correo electrónico del usuario

}
