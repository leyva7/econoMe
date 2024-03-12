package com.econoMe.gestorgastosback.repository;

import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountingRepository extends JpaRepository<Accounting, Long> {

    List<Accounting> findByUserCreator(User userCreator);
}
