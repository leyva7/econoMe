package com.econoMe.gestorgastosback.model;

import com.econoMe.gestorgastosback.common.OperationType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "operation")
public class Operations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "accounting_id", nullable = false)
    private Accounting accounting;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    private String description;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private OperationType type; // Enum para gasto o ingreso


    public Operations() {

    }

    public Operations(Accounting accounting, OperationType type, String description, Double quantity, LocalDate date, String category) {
        this.accounting = accounting;
        this.type = type;
        this.description = description;
        this.quantity = quantity;
        this.date = date;
        this.category = category;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public OperationType getType() {
        return type;
    }

    public void setType(OperationType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
