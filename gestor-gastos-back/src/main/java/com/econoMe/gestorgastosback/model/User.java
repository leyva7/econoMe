package com.econoMe.gestorgastosback.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    private String username;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    @Column(nullable = false, unique = true)
    private String mail;
    @Column(nullable = false)
    private String password;

    public User() {

    }

    // Constructor con todos los argumentos
    public User(String username, String name, String surname, String mail, String password) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.mail = mail;
        this.password = password;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getMail() {
        return mail;
    }

    public String getPassword() {
        return password;
    }

    // Setters

    public void setUsername(String username) {
        this.username = username;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

