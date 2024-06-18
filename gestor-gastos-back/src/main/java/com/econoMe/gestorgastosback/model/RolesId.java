package com.econoMe.gestorgastosback.model;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor // Constructor sin argumentos
public class RolesId implements Serializable {

    private Long user; // ID del usuario
    private Long accounting; // ID de la contabilidad

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolesId rolesId = (RolesId) o;
        return Objects.equals(user, rolesId.user) &&
                Objects.equals(accounting, rolesId.accounting);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, accounting);
    }
}
