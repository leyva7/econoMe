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
    private Role role;

}
