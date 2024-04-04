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
    private Long id;

    @ManyToOne
    @JoinColumn(name = "accounting_id", nullable = false)
    private Accounting accounting;

    @ManyToOne
    @JoinColumn(name = "user", nullable = false)
    private User user;

    private String description;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationType type;
}

