package com.econoMe.gestorgastosback.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RolesId implements Serializable {
    private Long user;
    private Long accounting;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolesId that = (RolesId) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(accounting, that.accounting);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, accounting);
    }
}
