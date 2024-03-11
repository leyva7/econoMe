package com.econoMe.gestorgastosback.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Operations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_accounting", referencedColumnName = "id")
    private Accounting accounting;

    @ManyToOne
    @JoinColumn(name = "id_category", referencedColumnName = "id")
    private Entrys category;

    private String description;
    private BigDecimal quantity;
    private LocalDate fecha;

    public Operations(Accounting accounting, Entrys category, String description, BigDecimal quantity, LocalDate fecha) {
        this.accounting = accounting;
        this.category = category;
        this.description = description;
        this.quantity = quantity;
        this.fecha = fecha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Accounting getAccounting() {
        return accounting;
    }

    public void setAccounting(Accounting accounting) {
        this.accounting = accounting;
    }

    public Entrys getCategory() {
        return category;
    }

    public void setCategory(Entrys category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
