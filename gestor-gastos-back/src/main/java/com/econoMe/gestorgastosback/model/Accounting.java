package com.econoMe.gestorgastosback.model;

import jakarta.persistence.*;

@Entity
@Table(name="accounting")
public class Accounting {

    public enum Type {
        PERSONAL, SHARED;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "creator_username", nullable = false)
    private User userCreator;

    @Enumerated(EnumType.STRING)
    private Type type;

    public Accounting() {

    }

    public Accounting(String name, String description, User userCreator) {
        this.name = name;
        this.description = description;
        this.userCreator = userCreator;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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
