package com.econoMe.gestorgastosback.repository;

import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.Roles;
import com.econoMe.gestorgastosback.model.RolesId;
import com.econoMe.gestorgastosback.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repositorio JPA para la entidad Roles
@Repository
public interface RolesRepository extends JpaRepository<Roles, RolesId> {

    // Métodos para realizar consultas personalizadas sobre la entidad Roles

    Optional<Roles> findByUserUsernameAndAccountingId(String username, Long accountingId); // Buscar roles por username y accountingId
    List<Roles> findAllByAccounting(Accounting accounting); // Buscar todos los roles por Accounting
    List<Roles> findAllByUser(User user); // Buscar todos los roles por User
    void deleteByAccounting(Accounting accounting); // Eliminar roles por Accounting
}
