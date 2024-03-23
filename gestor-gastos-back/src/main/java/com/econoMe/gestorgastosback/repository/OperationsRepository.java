package com.econoMe.gestorgastosback.repository;

import com.econoMe.gestorgastosback.model.Operations;
import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperationsRepository extends JpaRepository<Operations, Long> {

    List<Operations> findByAccounting(Accounting accounting);
    List<Operations> findByUser(User user);
    List<Operations> findByUserAndAccounting(User user, Accounting accounting);
    boolean existsByAccounting(Accounting accounting);
    boolean existsByUser(User user);
    void deleteByAccounting(Accounting accounting);
    void deleteByUser(User user);
}
