package com.econoMe.gestorgastosback.repository;

import com.econoMe.gestorgastosback.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Repositorio JPA para la entidad User
public interface UserRepository extends JpaRepository<User, Long> {

    // Métodos para realizar consultas personalizadas sobre la entidad User

    User findByMail(String mail); // Buscar usuario por correo electrónico

    Optional<User> findByUsername(String username); // Buscar usuario por nombre de usuario

    boolean existsByMail(String mail); // Verificar si existe un usuario con un correo electrónico específico

    boolean existsByUsername(String username); // Verificar si existe un usuario con un nombre de usuario específico
}
