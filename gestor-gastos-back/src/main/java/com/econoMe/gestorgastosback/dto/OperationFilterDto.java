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

    private String accountingId;     // ID de la contabilidad a filtrar

    private String type;             // Tipo de operación a filtrar (ingreso o gasto)

    private String category;         // Categoría de la operación a filtrar

    private Double quantityMin;      // Cantidad mínima de la operación a filtrar

    private Double quantityMax;      // Cantidad máxima de la operación a filtrar

    private LocalDate dateStart;     // Fecha de inicio para el filtro por rango de fechas

    private LocalDate dateEnd;       // Fecha de fin para el filtro por rango de fechas

}
