package com.econoMe.gestorgastosback.repository;

import com.econoMe.gestorgastosback.model.Roles;
import com.econoMe.gestorgastosback.model.RolesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Roles, RolesId> {

    Optional<Roles> findByUserUsernameAndAccountingId(String username, Long accountingId);
}
