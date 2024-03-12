package com.econoMe.gestorgastosback.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "operation")
public class Operations {
        public enum OperationType {
            SPENT, INCOME
        }

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "accounting_id", nullable = false)
        private Accounting accounting;

        @Column(nullable = false)
        private String description;

        @Column(nullable = false)
        private BigDecimal quantity;

        @Column(nullable = false)
        private LocalDate date;

        @Enumerated(EnumType.STRING)
        private OperationType type; // Enum para gasto o ingreso



        public Operations() {

        }

        public Operations(Accounting accounting, OperationType type, String description, BigDecimal quantity, LocalDate date) {
            this.accounting = accounting;
            this.type = type;
            this.description = description;
            this.quantity = quantity;
            this.date = date;
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

        public BigDecimal getQuantity() {
            return quantity;
        }

        public void setQuantity(BigDecimal quantity) {
            this.quantity = quantity;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }
}
