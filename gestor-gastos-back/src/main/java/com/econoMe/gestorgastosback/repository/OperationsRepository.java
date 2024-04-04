package com.econoMe.gestorgastosback.repository;

import com.econoMe.gestorgastosback.common.OperationType;
import com.econoMe.gestorgastosback.model.Operations;
import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperationsRepository extends JpaRepository<Operations, Long> {

    List<Operations> findByAccounting(Accounting accounting);
    List<Operations> findByUser(User user);
    List<Operations> findByUserAndAccounting(User user, Accounting accounting);
    List<Operations> findByAccountingAndType(Accounting accounting, OperationType type);
    void deleteByAccounting(Accounting accounting);

}
