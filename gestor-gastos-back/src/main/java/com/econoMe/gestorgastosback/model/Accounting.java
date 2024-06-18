package com.econoMe.gestorgastosback.model;

import jakarta.persistence.*;
import lombok.*;
import com.econoMe.gestorgastosback.common.Type;

@Entity
@Table(name = "accounting")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Accounting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador único de la contabilidad

    @Column(nullable = false)
    private String name; // Nombre de la contabilidad

    @Column
    private String description; // Descripción de la contabilidad

    @ManyToOne
    @JoinColumn(name = "user_creator", nullable = false)
    private User userCreator; // Usuario creador de la contabilidad

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type; // Tipo de la contabilidad (enum)
}
