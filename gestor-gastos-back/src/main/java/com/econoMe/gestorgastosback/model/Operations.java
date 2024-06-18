package com.econoMe.gestorgastosback.model;

import com.econoMe.gestorgastosback.common.OperationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "operation")
@Getter
@Setter
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor
public class Operations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador único de la operación

    @ManyToOne
    @JoinColumn(name = "accounting_id", nullable = false)
    private Accounting accounting; // Contabilidad asociada a la operación

    @ManyToOne
    @JoinColumn(name = "user", nullable = false)
    private User user; // Usuario asociado a la operación

    private String description; // Descripción de la operación

    @Column(nullable = false)
    private Double quantity; // Cantidad de la operación

    @Column(nullable = false)
    private String category; // Categoría de la operación

    @Column(nullable = false)
    private LocalDate date; // Fecha de la operación

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationType type; // Tipo de la operación (enum)
}
