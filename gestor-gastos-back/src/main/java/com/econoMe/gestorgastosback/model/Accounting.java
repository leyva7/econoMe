package com.econoMe.gestorgastosback.model;

import jakarta.persistence.*;

@Entity
public class Accounting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "id_creator", referencedColumnName = "id")
    private User userCreator;

    public Accounting(String name, String description, User userCreator) {
        this.name = name;
        this.description = description;
        this.userCreator = userCreator;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUserCreator() {
        return userCreator;
    }

    public void setUserCreator(User userCreator) {
        this.userCreator = userCreator;
    }
}
