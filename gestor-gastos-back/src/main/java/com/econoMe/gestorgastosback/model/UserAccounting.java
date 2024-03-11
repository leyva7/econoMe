package com.econoMe.gestorgastosback.model;

import jakarta.persistence.*;

@Entity
public class UserAccounting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_accounting", referencedColumnName = "id")
    private Accounting accounting;

    private String rol;

    public UserAccounting(User user, Accounting accounting, String rol) {
        this.user = user;
        this.accounting = accounting;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Accounting getAccounting() {
        return accounting;
    }

    public void setAccounting(Accounting accounting) {
        this.accounting = accounting;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
