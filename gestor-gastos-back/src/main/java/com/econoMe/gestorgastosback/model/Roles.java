package com.econoMe.gestorgastosback.model;

import com.econoMe.gestorgastosback.common.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@IdClass(RolesId.class)
@Getter
@Setter
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor
public class Roles {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_username", nullable = false)
    private User user; // Usuario asociado al rol

    @Id
    @ManyToOne
    @JoinColumn(name = "accounting_id", nullable = false)
    private Accounting accounting; // Contabilidad asociada al rol

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // Rol del usuario en la contabilidad (enum)
}
