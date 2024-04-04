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
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_creator", nullable = false)
    private User userCreator;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;
}