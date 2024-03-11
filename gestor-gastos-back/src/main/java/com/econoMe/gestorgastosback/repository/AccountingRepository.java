package com.econoMe.gestorgastosback.repository;

import com.econoMe.gestorgastosback.model.Accounting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountingRepository extends JpaRepository<Accounting, Long> {
}
