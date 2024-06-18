package com.econoMe.gestorgastosback.dto;

import com.econoMe.gestorgastosback.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDto {

    private User user;                 // Datos del usuario a registrar

    private AccountingDto accounting;  // Datos de la contabilidad asociada al usuario

}
