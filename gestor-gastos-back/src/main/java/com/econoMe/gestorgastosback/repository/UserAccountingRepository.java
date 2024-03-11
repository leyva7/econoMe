package com.econoMe.gestorgastosback.repository;

import com.econoMe.gestorgastosback.model.UserAccounting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountingRepository extends JpaRepository<UserAccounting, Long> {
}
