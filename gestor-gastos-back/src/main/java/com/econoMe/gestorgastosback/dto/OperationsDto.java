package com.econoMe.gestorgastosback.dto;

import com.econoMe.gestorgastosback.common.OperationType;
import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationsDto {

    private String accountingName;

    private String username;

    private String description;

    private BigDecimal quantity;

    private String category;

    private LocalDate date;

    private OperationType type;


}
