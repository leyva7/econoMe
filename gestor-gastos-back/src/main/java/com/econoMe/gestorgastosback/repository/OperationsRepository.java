package com.econoMe.gestorgastosback.repository;

import com.econoMe.gestorgastosback.common.OperationType;
import com.econoMe.gestorgastosback.model.Operations;
import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Repositorio JPA para la entidad Operations
public interface OperationsRepository extends JpaRepository<Operations, Long> {

    // Métodos para realizar consultas personalizadas sobre la entidad Operations

    List<Operations> findByAccounting(Accounting accounting); // Buscar operaciones por Accounting
    List<Operations> findByUser(User user); // Buscar operaciones por User
    List<Operations> findByUserAndAccounting(User user, Accounting accounting); // Buscar operaciones por User y Accounting
    List<Operations> findByAccountingAndType(Accounting accounting, OperationType type); // Buscar operaciones por Accounting y Type
    void deleteByAccounting(Accounting accounting); // Eliminar operaciones por Accounting
    void deleteByUser(User user);
}
