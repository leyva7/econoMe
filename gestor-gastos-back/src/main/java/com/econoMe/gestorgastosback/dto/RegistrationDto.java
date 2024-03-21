package com.econoMe.gestorgastosback.dto;

import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.User;

public class RegistrationDto {
    private User user;
    private AccountingDto accounting;
    public RegistrationDto() {}

    public RegistrationDto(User user, AccountingDto accounting) {
        this.user = user;
        this.accounting = accounting;
    }

    // Getters y setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AccountingDto getAccounting() {
        return accounting;
    }

    public void setAccounting(AccountingDto accounting) {
        this.accounting = accounting;
    }
}
