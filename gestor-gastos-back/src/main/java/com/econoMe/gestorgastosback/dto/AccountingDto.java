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
public class AccountingDto {

    private Long id;                // Identificador único de la contabilidad

    private String name;            // Nombre de la contabilidad

    private String description;     // Descripción de la contabilidad

    private String userCreator;     // Usuario creador de la contabilidad

    private Type type;              // Tipo de contabilidad (personal o compartida)

}
