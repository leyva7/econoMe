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
    private String name;

    private String description;

    private String userCreator;

    private Type type;
}
