package com.econoMe.gestorgastosback.repository;

import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Repositorio JPA para la entidad Accounting
public interface AccountingRepository extends JpaRepository<Accounting, Long> {

    // Método para buscar todas las Accounting creadas por un User específico
    List<Accounting> findByUserCreator(User userCreator);

}
