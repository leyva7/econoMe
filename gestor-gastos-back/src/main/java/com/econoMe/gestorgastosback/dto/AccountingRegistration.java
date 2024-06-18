package com.econoMe.gestorgastosback.dto;

import com.econoMe.gestorgastosback.common.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountingRegistration {

    private String name;            // Nombre de la contabilidad a registrar

    private String description;     // Descripción de la contabilidad a registrar

    private String userCreator;     // Usuario creador de la contabilidad

    private Type type;              // Tipo de contabilidad (personal o compartida)

}
