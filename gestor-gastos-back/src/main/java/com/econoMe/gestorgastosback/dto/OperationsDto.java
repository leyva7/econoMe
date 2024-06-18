package com.econoMe.gestorgastosback.dto;

import com.econoMe.gestorgastosback.common.OperationType;
import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationsDto {

    private Long id;                   // Identificador único de la operación

    private Long accountingId;         // ID de la contabilidad asociada a la operación

    private String username;           // Nombre de usuario asociado a la operación

    private String description;        // Descripción de la operación

    private Double quantity;           // Cantidad de la operación

    private String category;           // Categoría de la operación

    @JsonFormat(pattern = "dd-MM-yyyy")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;            // Fecha de la operación

    private OperationType type;        // Tipo de la operación (Ingreso o Gasto)

}
