package com.econoMe.gestorgastosback.model;

import com.econoMe.gestorgastosback.common.Role;
import jakarta.persistence.*;

@Entity
@IdClass(RolesId.class)
public class Roles {

        @Id
        @ManyToOne
        @JoinColumn(name = "user_username")
        private User user;

        @Id
        @ManyToOne
        @JoinColumn(name = "accounting_id")
        private Accounting accounting;
        @Enumerated(EnumType.STRING)
        private Role role; // Enum de roles

        public Roles() {

        }

        public Roles(User user, Accounting accounting, Role role) {
            this.user = user;
            this.accounting = accounting;
            this.role = role;
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

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }
}
