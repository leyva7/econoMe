package com.econoMe.gestorgastosback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    String token;     // Token de autenticación generado para el usuario

    String username;  // Nombre de usuario asociado al token

}
