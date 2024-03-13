package com.econoMe.gestorgastosback.repository;

import com.econoMe.gestorgastosback.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    User findByMail(String mail);

    Optional<User> findByUsername(String username);

    boolean existsByMail(String mail);

    boolean existsByUsername(String username);
}
