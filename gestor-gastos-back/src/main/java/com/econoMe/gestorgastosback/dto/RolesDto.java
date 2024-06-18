package com.econoMe.gestorgastosback.dto;

import com.econoMe.gestorgastosback.common.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolesDto {

    private String username;     // Nombre de usuario asociado al rol

    private Long accountingId;   // ID de la contabilidad asociada al rol

    private Role role;           // Rol del usuario en la contabilidad (VISUALIZER o EDITOR)

}
