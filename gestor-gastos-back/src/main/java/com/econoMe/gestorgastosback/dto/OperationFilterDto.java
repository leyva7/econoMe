package com.econoMe.gestorgastosback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationFilterDto {
    private String accountingId;
    private String type;
    private String category;
    private Double quantityMin;
    private Double quantityMax;
    private LocalDate dateStart;
    private LocalDate dateEnd;

}