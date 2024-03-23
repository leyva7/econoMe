package com.econoMe.gestorgastosback.model;

import java.io.Serializable;
import java.util.Objects;

public class RolesId implements Serializable {
    private Long user;
    private Long accounting;

    public RolesId() {
    }

    public RolesId(Long user, Long accountingId) {
        this.user = user;
        this.accounting = accountingId;
    }

    public Long getUsername() {
        return user;
    }

    public void setUsername(Long user) {
        this.user = user;
    }

    public Long getAccountingId() {
        return accounting;
    }

    public void setAccountingId(Long accountingId) {
        this.accounting = accountingId;
    }

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
