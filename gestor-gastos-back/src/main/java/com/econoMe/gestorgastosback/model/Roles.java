package com.econoMe.gestorgastosback.model;

import com.econoMe.gestorgastosback.common.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@IdClass(RolesId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Roles {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_username", nullable = false)
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "accounting_id", nullable = false)
    private Accounting accounting;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // Enum de roles
}