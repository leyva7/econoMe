package com.econoMe.gestorgastosback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {

    private String username;  // Nombre de usuario para la autenticación

    private String password;  // Contraseña para la autenticación

}
